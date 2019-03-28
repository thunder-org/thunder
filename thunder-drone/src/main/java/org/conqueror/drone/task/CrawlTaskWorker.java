package org.conqueror.drone.task;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.event.LoggingAdapter;
import org.conqueror.drone.config.CrawlConfig;
import org.conqueror.drone.data.messages.CrawlSourceAssignRequest;
import org.conqueror.drone.data.messages.CrawlSourceAssignResponse;
import org.conqueror.drone.data.url.URLInfo;
import org.conqueror.drone.messages.PageCrawlingEnd;
import org.conqueror.drone.selenium.webdriver.WebBrowser;
import org.conqueror.lion.cluster.task.TaskWorker;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.JobManagerMessage;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;


public class CrawlTaskWorker extends TaskWorker<CrawlConfig, CrawlSourceAssignResponse> {

    private final WebBrowser browser;

    private int sourceSize = 0;

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
    public Receive createReceive() {
        return super.createReceive()
            .orElse(
                receiveBuilder()
                    .match(PageCrawlingEnd.class, this::processPageCrawlingEnd)
                    .build()
            );
    }

    protected void processPageCrawlingEnd(PageCrawlingEnd end) {
        if (end.getNumber() == sourceSize) {
            requestTask();
        }
    }

    @Override
    protected void processAssignedTask(JobManagerMessage.TaskAssignResponse response) throws Exception {
        work((CrawlSourceAssignResponse) response);
    }

    @Override
    protected void work(CrawlSourceAssignResponse source) throws Exception {
        sourceSize = source.getSources().size();

        int sourceNumber = 0;
        Cancellable schedule = null;
        for (URLInfo urlInfo : source.getSources()) {
            PageCrawler pageCrawler = (PageCrawler) Class.forName(getConfig().getPageCrawlerClass())
                .getConstructor(int.class, Cancellable.class, WebBrowser.class, LoggingAdapter.class, URLInfo.class, CrawlConfig.class, ActorRef.class, ActorRef.class)
                .newInstance(++sourceNumber, schedule, getBrowser(), log(), urlInfo, getConfig(), getTaskManager(), getSelf());

            int delay = getConfig().getIntervalSecs() * sourceNumber;
            schedule = getContext().getSystem().getScheduler().schedule(
                Duration.ofSeconds(delay)
                , Duration.ofSeconds(1)
                , pageCrawler
                , getContext().getSystem().dispatcher()
            );
            pageCrawler.setSchedule(schedule);
        }
    }

    protected WebBrowser getBrowser() {
        return browser;
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

}
