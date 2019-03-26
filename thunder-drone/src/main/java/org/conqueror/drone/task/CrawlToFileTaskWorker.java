package org.conqueror.drone.task;

import akka.actor.ActorRef;
import org.conqueror.common.utils.file.FileUtils;
import org.conqueror.drone.data.url.URLInfo;
import org.conqueror.lion.config.JobConfig;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class CrawlToFileTaskWorker extends CrawlTaskWorker {

    public CrawlToFileTaskWorker(JobConfig config, ActorRef taskManager) {
        super(config, taskManager);
    }

    protected void savePageSource(URLInfo urlInfo) {
        if (getConfig().getRootDirectory() == null) return;

        int numberOfPages = getConfig().getNumberOfPages();
//        String pageSource = getBrowser().getPageSource().trim();
//        if (pageSource.isEmpty()) return;

        WebElement title = getBrowser().findElementByXPath("//*[@id=\"articleTitle\"]", 3);
        if (title == null) return;
        WebElement postDate = getBrowser().findElementByXPath("//*[@id=\"main_content\"]/div[1]/div[3]/div/span[2]", 3);
        if (postDate == null) return;
        WebElement body = getBrowser().findElementByXPath("//*[@id=\"articleBodyContents\"]", 3);
        if (body == null) return;

        try {
            int fileNumber = urlInfo.getId() / numberOfPages;
            FileUtils.createDirectory(getConfig().getRootDirectory(), urlInfo.getDomain());
            Path filePath = Paths.get(getConfig().getRootDirectory(), urlInfo.getDomain(), String.valueOf(fileNumber));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", urlInfo.getId());
            jsonObject.put("depth", urlInfo.getDepth());
            jsonObject.put("url", urlInfo.getUrl());
            jsonObject.put("title", title.getText());
            jsonObject.put("postDate", postDate.getText());
            jsonObject.put("body", body.getText());

            FileUtils.writeContent(filePath, jsonObject.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            FileUtils.writeContent(filePath, "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log().error(e, "failed to save page source");
        }
    }

}
