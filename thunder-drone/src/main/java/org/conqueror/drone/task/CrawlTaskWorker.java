package org.conqueror.drone.task;

import akka.actor.ActorRef;
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
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CrawlTaskWorker extends TaskWorker<CrawlConfig, CrawlSourceAssignResponse> {

    private final WebBrowser browser;

    private final Set<String> extractedUrls = new HashSet<>();

    public CrawlTaskWorker(JobConfig config, ActorRef taskManager) {
        super(config, taskManager);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars"); // disabling infobars
        options.addArguments("--dns-prefetch-disable"); // disabling infobars
        options.addArguments("--always-authorize-plugins"); // disabling infobars
//        options.addArguments("--headless"; // disabling infobars
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // Bypass OS security model

        browser = WebBrowser.createWebBrowser(WebBrowser.WebDriverName.CHROME, options);
    }

    @Override
    protected void work(CrawlSourceAssignResponse source) throws Exception {
        for (URLInfo urlInfo : source.getSources()) {
            List<URLInfo> childUrls = new ArrayList<>();
            System.out.println(urlInfo);
            browser.visit(urlInfo.getUrl());
            browser.waitForPageLoad();
//            browser.getPageSource();

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

                        String onclick = anchor.getAttribute("onclick");
                        if (onclick != null || (url != null && url.startsWith("javascript"))) {
                            WebDriver driver = browser.getDriver();
//                            new Actions(driver).moveToElement(anchor).click().perform();
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
                } catch (Exception ignored) {
                }

                if (!browser.getCurrentUrl().equals(urlInfo.getUrl())) {
                    browser.executeJavascript(String.format("location.href='%s';", urlInfo.getUrl()));
                }
            }

            sendToTaskManager(new UrlInfos(childUrls));
            extractedUrls.clear();
        }
    }

    private boolean addChildUrl(List<URLInfo> childUrls, String url, URLInfo urlInfo) {
        if (url != null && URLInfo.isURI(url) && urlInfo.equalsDomain(url)) {
            url = URLInfo.normalize(url, false);
            if (!extractedUrls.contains(url)) {
                childUrls.add(new URLInfo(urlInfo.getDomain(), url, urlInfo.getDepth() + 1));
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

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Hyundai\\Downloads\\chromedriver_win32\\chromedriver.exe");

        WebBrowser browser = WebBrowser.createWebBrowser(WebBrowser.WebDriverName.CHROME);
        browser.visit("https://www.geogigani.com/");
        String source = browser.getPageSource();
        WebElement element = browser.findElementByXPath("//*[@id=\"popular-ac-tab-1-0\"]/div/div[1]/div/a", 3);
        element.click();
//            String url = element.getAttribute("href");
//            System.out.println(url);
//            url = URLInfo.normalize(url, false);
//            System.out.println(url);
    }

}
