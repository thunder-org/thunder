package org.conqueror.drone.task;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import org.apache.poi.ss.formula.functions.T;
import org.conqueror.drone.config.CrawlConfig;
import org.conqueror.drone.data.messages.CrawlSourceAssignRequest;
import org.conqueror.drone.data.messages.CrawlSourceAssignResponse;
import org.conqueror.drone.data.messages.UrlInfos;
import org.conqueror.drone.data.url.URLInfo;
import org.conqueror.drone.selenium.webdriver.WebBrowser;
import org.conqueror.lion.cluster.task.TaskWorker;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.JobManagerMessage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class CrawlTaskWorker extends TaskWorker<CrawlConfig, CrawlSourceAssignResponse> {

    private final WebBrowser browser;

    private final Set<String> extractedUrls = new HashSet<>();

    private Cancellable crawling;

    public CrawlTaskWorker(JobConfig config, ActorRef taskManager) {
        super(config, taskManager);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars"); // disabling infobars
        options.addArguments("--dns-prefetch-disable"); // disabling infobars
        options.addArguments("--always-authorize-plugins"); // disabling infobars
        options.addArguments("--headless"); // disabling infobars
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // Bypass OS security model

        browser = WebBrowser.createWebBrowser(WebBrowser.WebDriverName.CHROME, options);
    }

    @Override
    protected void processAssignedTask(JobManagerMessage.TaskAssignResponse response) throws Exception {
        work((CrawlSourceAssignResponse) response);
    }

    @Override
    protected void work(CrawlSourceAssignResponse source) throws Exception {

        int sourceNumber = 0;
        AtomicInteger startedNumber = new AtomicInteger(0);
        for (URLInfo urlInfo : source.getSources()) {
//            crawl(urlInfo);

//            extractedUrls.clear();

            int delay = getConfig().getIntervalSecs() * ++sourceNumber;
            crawling = getContext().getSystem().getScheduler().scheduleOnce(Duration.ofSeconds(delay)
                , () -> {
                    int number = startedNumber.addAndGet(1);
                    crawl(urlInfo);
                    extractedUrls.clear();

                    if (number == source.getSources().size()) {
                        requestTask();
                    }
                }
                , getContext().getSystem().dispatcher()
            );

        }
    }

    protected WebBrowser getBrowser() {
        return browser;
    }

    private void crawl(URLInfo urlInfo) {
        log().info("visit url : {}, id : {}", urlInfo.getUrl(), urlInfo.getId());
        browser.visit(urlInfo.getUrl());
        browser.waitForPageLoad();

        savePageSource(urlInfo);

        // extract urls
        if (getConfig().getDepth() == 0 || urlInfo.getDepth() < getConfig().getDepth()) {
            List<URLInfo> childUrls = extractChildUrls(urlInfo);
            sendToTaskManager(new UrlInfos(childUrls));
        }
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
                log().error(e, "failed to extract url : {}", anchorXpath);
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
            browser.waitForPageLoad();

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

    protected abstract void savePageSource(URLInfo urlInfo);

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

    @Override
    protected JobManagerMessage.TaskAssignRequest createTaskAssignRequest() {
        return new CrawlSourceAssignRequest(getConfig().getNumberOfSources());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        browser.close();
    }

    public static void main(String[] args) throws IOException {
//        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Hyundai\\Downloads\\chromedriver_win32\\chromedriver.exe");

//        WebBrowser browser = WebBrowser.createWebBrowser(WebBrowser.WebDriverName.CHROME);
//        browser.visit("https://news.naver.com/main/main.nhn?mode=LSD&mid=shm&sid1=105");
//        String source = browser.getPageSource();
//        WebElement element = browser.findElementByXPath("//*[@id=\"popular-ac-tab-1-0\"]/div/div[1]/div/a", 3);
//        element.click();
//            String url = element.getAttribute("href");
//            System.out.println(url);
//            url = URLInfo.normalize(url, false);
//            System.out.println(url);
        final StringBuilder sb = new StringBuilder();
        HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback() {
            public boolean readyForNewline;

            @Override
            public void handleText(final char[] data, final int pos) {
                String s = new String(data);
                sb.append(s.trim());
                readyForNewline = true;
            }

            @Override
            public void handleStartTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
                if (readyForNewline && (t == HTML.Tag.DIV || t == HTML.Tag.BR || t == HTML.Tag.P)) {
                    sb.append("\n");
                    readyForNewline = false;
                }
            }

            @Override
            public void handleSimpleTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
                handleStartTag(t, a, pos);
            }
        };
        String html = "<head><title>[윤진우의 부루마블] 게임 내치는 중국 정부에…해외로 눈돌리는 텐센트 : 네이버 뉴스</title></head>";
        new ParserDelegator().parse(new StringReader(html), parserCallback, false);
        System.out.println(sb);
    }

}
