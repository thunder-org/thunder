package org.conqueror.drone.task;

import akka.actor.ActorRef;
import org.conqueror.drone.config.CrawlConfig;
import org.conqueror.drone.data.messages.UrlInfos;
import org.conqueror.lion.cluster.task.TaskManager;
import org.conqueror.lion.config.JobConfig;


public class CrawlTaskManager extends TaskManager<CrawlConfig> {

    public CrawlTaskManager(JobConfig config, ActorRef master, ActorRef jobManager) {
        super(config, master, jobManager);

        initSystemProperties();
    }

    public Receive createReceive() {
        return super.createReceive().orElse(receiveBuilder()
            .match(UrlInfos.class, this::processExtractedUrl)
            .matchAny(message -> {
                log().warning("received an unhandled request : {}", message);
                unhandled(message);
            })
            .build());
    }

    @Override
    protected void prepareJob() throws Exception {
        System.out.println("[start] crawl-task-manager");
    }

    @Override
    protected void finishJob() throws Exception {
        System.out.println("[end] crawl-task-manager");
    }

    protected void processExtractedUrl(UrlInfos urls) {
        tellToJobManager(urls);
    }

    private void initSystemProperties() {
        System.setProperty("webdriver.chrome.driver", getConfig().getChromeWebDriver());
    }

}
