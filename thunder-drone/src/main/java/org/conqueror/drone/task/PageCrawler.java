package org.conqueror.drone.task;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.event.LoggingAdapter;
import org.conqueror.drone.config.CrawlConfig;
import org.conqueror.drone.data.messages.UrlInfos;
import org.conqueror.drone.data.url.URLInfo;
import org.conqueror.drone.messages.PageCrawlingEnd;
import org.conqueror.drone.selenium.webdriver.WebBrowser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class PageCrawler implements Runnable {

    private Cancellable schedule = null;
    private int number;
    private Cancellable beforeSchedule;
    private WebBrowser browser;
    private LoggingAdapter logger;
    private URLInfo urlInfo;
    private CrawlConfig config;
    private ActorRef taskManager;
    private ActorRef taskWorker;

    private final Set<String> extractedUrls = new HashSet<>();

    public PageCrawler(int number, Cancellable beforeSchedule, WebBrowser browser, LoggingAdapter logger, URLInfo urlInfo, CrawlConfig config, ActorRef taskManager, ActorRef taskWorker) {
        this.number = number;
        this.beforeSchedule = beforeSchedule;
        this.browser = browser;
        this.logger = logger;
        this.urlInfo = urlInfo;
        this.config = config;
        this.taskManager = taskManager;
        this.taskWorker = taskWorker;
    }

    @Override
    public void run() {
        if (isStoppedBeforeSchedule()) {
            try {
                crawl(urlInfo);

                extractedUrls.clear();
            } finally {
                stopSchedule();
            }
        }
    }

    public void setSchedule(Cancellable schedule) {
        this.schedule = schedule;
    }

    protected void crawl(URLInfo urlInfo) {
        log().info("visit url : {}, id : {}", urlInfo.getUrl(), urlInfo.getId());
        try {
            browser.visit(urlInfo.getUrl());
//        browser.waitForPageLoad();

            savePageSource(urlInfo);

            // extract urls
            if (getConfig().getDepth() == 0 || urlInfo.getDepth() < getConfig().getDepth()) {
                List<URLInfo> childUrls = extractChildUrls(urlInfo);
                sendToTaskManager(new UrlInfos(childUrls));
            }

        } finally {
            taskWorker.tell(new PageCrawlingEnd(getNumber()), taskWorker);
        }
    }

    protected abstract void savePageSource(URLInfo urlInfo);

    protected int getNumber() {
        return number;
    }

    protected CrawlConfig getConfig() {
        return config;
    }

    protected LoggingAdapter log() {
        return logger;
    }

    protected WebBrowser getBrowser() {
        return browser;
    }

    private boolean isStoppedBeforeSchedule() {
        return beforeSchedule == null || beforeSchedule.isCancelled();
    }

    private void stopSchedule() {
        if (schedule != null) schedule.cancel();
    }

    private List<URLInfo> extractChildUrls(URLInfo urlInfo) {
        List<URLInfo> childUrls = new ArrayList<>();
        List<WebElement> anchors = browser.findElementsByTag("a");
        List<String> anchorXpaths = new ArrayList<>(anchors.size());
        for (WebElement anchor : anchors) {
            anchorXpaths.add(browser.getXPathFromElement(anchor));
        }

        for (String anchorXpath : anchorXpaths) {
            try {
                WebElement anchor = browser.findElementByXPath(anchorXpath, 3);
                if (anchor != null && anchor.isDisplayed() && anchor.isEnabled()) {
                    String url = anchor.getAttribute("href");
                    if (addChildUrl(childUrls, url, urlInfo)) continue;

                    click(anchor, url, childUrls, urlInfo);
                }
            } catch (Exception e) {
                logger.error(e, "failed to extract url : {}", anchorXpath);
            }

            if (!browser.getCurrentUrl().equals(urlInfo.getUrl())) {
                browser.executeJavascript(String.format("location.href='%s';", urlInfo.getUrl()));
            }
        }

        return childUrls;
    }

    private void click(WebElement anchor, String url, List<URLInfo> childUrls, URLInfo urlInfo) {
        String onclick = anchor.getAttribute("onclick");
        if (onclick != null || (url != null && url.startsWith("javascript"))) {
            WebDriver driver = browser.getDriver();
            browser.executeJavascript("arguments[0].click();", anchor);
//            browser.waitForPageLoad();

            // opened in new window/tab
            String currentWindowHandle = driver.getWindowHandle();
            Set<String> windowHandles = driver.getWindowHandles();
            if (windowHandles.size() > 1) {
                windowHandles.remove(currentWindowHandle);
                for (String newWindowHandle : windowHandles) {
                    driver.switchTo().window(newWindowHandle);
                    url = browser.getCurrentUrl();
                    addChildUrl(childUrls, url, urlInfo);
                    driver.close();
                }
                driver.switchTo().window(currentWindowHandle);
            } else {
                url = browser.getCurrentUrl();
                addChildUrl(childUrls, url, urlInfo);
                browser.executeJavascript(String.format("location.href='%s';", urlInfo.getUrl()));
            }

        }
    }

    private boolean addChildUrl(List<URLInfo> childUrls, String url, URLInfo urlInfo) {
        if (url != null && URLInfo.isURI(url) && urlInfo.equalsDomain(url)) {
            url = URLInfo.normalize(url, false);
            if (!extractedUrls.contains(url)) {
                childUrls.add(new URLInfo(0, urlInfo.getDomain(), url, urlInfo.getDepth() + 1));
                extractedUrls.add(url);
                return true;
            }
        }

        return false;
    }

    private void sendToTaskManager(UrlInfos urlInfos) {
        taskManager.tell(urlInfos, taskWorker);
    }

}
