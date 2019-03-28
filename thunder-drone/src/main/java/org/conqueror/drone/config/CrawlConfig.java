package org.conqueror.drone.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.common.exceptions.serialize.SerializableException;

import java.io.DataInput;
import java.io.IOException;


public class CrawlConfig extends JobConfig<CrawlConfig> {

    /* url db */
    private String jdbcUrl = null;
    private String user = null;
    private String password = null;
    private int sizeOfUrlBuffer = 0;

    /* crawling */
    private String pageCrawlerClass = null;

    /* crawled contents */
    private String rootDirectory = null;
    private int numberOfPages = 0;

    /* crawling site */
    private String group = null;
    private String domain = null;
    private String seed = null;
    private int numberOfSources = 0;
    private int depth = 0;  // 0:infinite, 1~:n-depth
    private boolean includeFragment = false;
    private int intervalSecs = 0;

    /* selenium */
    private String chromeWebDriver = null;

    private int requestWaitingTimeSec = 0;

    public CrawlConfig() {
        super();
    }

    public CrawlConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public CrawlConfig(Config config) {
        super(config);
        buildConfig(config);
    }

    private void buildConfig(Config config) {
        setJdbcUrl(getStringFromConfig(config, "job.crawl.url.db.jdbc", true));
        setUser(getStringFromConfig(config, "job.crawl.url.db.user", true));
        setPassword(getStringFromConfig(config, "job.crawl.url.db.password", true));
        setSizeOfUrlBuffer(getIntegerFromConfig(config, "job.crawl.url.buffer.size", 1000));

        setPageCrawlerClass(getStringFromConfig(config, "job.page-crawler.class", true));

        setRootDirectory(getStringFromConfig(config, "job.crawl.page.root-directory", null));
        setNumberOfPages(getIntegerFromConfig(config, "job.crawl.page.file.page-size", 100));

        setGroup(getStringFromConfig(config, "job.crawl.site.group", true));
        setDomain(getStringFromConfig(config, "job.crawl.site.domain", true));
        setSeed(getStringFromConfig(config, "job.crawl.site.seed", true));
        setNumberOfSources(getIntegerFromConfig(config, "job.crawl.site.source-number", 10));
        setDepth(getIntegerFromConfig(config, "job.crawl.site.filter.depth", true));
        setIncludeFragment(getBooleanFromConfig(config, "job.crawl.site.url.include-fragment", true));
        setIntervalSecs(getIntegerFromConfig(config, "job.crawl.site.visit.interval", 0));

        setChromeWebDriver(getStringFromConfig(config, "job.crawl.selenium.webdriver.chrome.driver", true));

        setRequestWaitingTimeSec(getIntegerFromConfig(config, "job.crawl.request.waiting-time", 10));
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSizeOfUrlBuffer() {
        return sizeOfUrlBuffer;
    }

    public void setSizeOfUrlBuffer(int sizeOfUrlBuffer) {
        this.sizeOfUrlBuffer = sizeOfUrlBuffer;
    }

    public String getPageCrawlerClass() {
        return pageCrawlerClass;
    }

    public void setPageCrawlerClass(String pageCrawlerClass) {
        this.pageCrawlerClass = pageCrawlerClass;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public int getNumberOfSources() {
        return numberOfSources;
    }

    public void setNumberOfSources(int numberOfSources) {
        this.numberOfSources = numberOfSources;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isIncludeFragment() {
        return includeFragment;
    }

    public void setIncludeFragment(boolean includeFragment) {
        this.includeFragment = includeFragment;
    }

    public int getIntervalSecs() {
        return intervalSecs;
    }

    public void setIntervalSecs(int intervalSecs) {
        this.intervalSecs = intervalSecs;
    }

    public String getChromeWebDriver() {
        return chromeWebDriver;
    }

    public void setChromeWebDriver(String chromeWebDriver) {
        this.chromeWebDriver = chromeWebDriver;
    }

    public int getRequestWaitingTimeSec() {
        return requestWaitingTimeSec;
    }

    public void setRequestWaitingTimeSec(int requestWaitingTimeSec) {
        this.requestWaitingTimeSec = requestWaitingTimeSec;
    }

    @Override
    public CrawlConfig readObject(DataInput input) throws SerializableException {
        try {
            return new CrawlConfig(ConfigFactory.parseString(input.readUTF()));
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }


}
