package org.conqueror.drone.job;

import akka.actor.ActorRef;
import org.conqueror.common.utils.db.DBConnector;
import org.conqueror.drone.config.CrawlConfig;
import org.conqueror.drone.data.messages.CrawlSourceAssignRequest;
import org.conqueror.drone.data.messages.CrawlSourceAssignResponse;
import org.conqueror.drone.data.messages.UrlInfos;
import org.conqueror.drone.data.url.URLInfo;
import org.conqueror.lion.cluster.job.JobManager;
import org.conqueror.lion.message.JobManagerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class CrawlJobManager extends JobManager<CrawlConfig> {

    private final Map<String, Boolean> requestingTaskManagers;

    private final DBConnector db;

    private static final String GROUPS_TABLE_NAME = "groups";

    private final String group;
    private final String domain;
    private final String seed;
    private final String table;

    private ResultSet urls = null;
    private int lastID = 0;

    public CrawlJobManager(CrawlConfig config, ActorRef master) throws SQLException {
        super(config, master);
        Properties prop = new Properties();
        prop.setProperty("user", config.getUser());
        prop.setProperty("password", config.getPassword());
        db = new DBConnector(config.getJdbcUrl(), prop);

        requestingTaskManagers = new HashMap<>(config.getMaxNumberOfTaskManagers() * config.getMaxNumberOfTaskWorkers());

        group = getConfig().getGroup();
        domain = getConfig().getDomain();
        seed = getConfig().getSeed();
        table = domain.replace('.', '_');
    }

    public Receive createReceive() {
        return super.createReceive().orElse(receiveBuilder()
            .match(UrlInfos.class, this::processExtractedUrls)
            .matchAny(message -> {
                log().warning("received an unhandled request : {}", message);
                unhandled(message);
            })
            .build());
    }

    @Override
    protected void prepareJob() throws Exception {
        System.out.println("[start] crawl-job-manager");

        createGroupsTable();
        insertGroup(group, domain);

        createDomainTable(table);
        insertSeed(table, seed);
    }

    @Override
    protected void finishJob() throws Exception {
        System.out.println("[end] crawl-job-manager");
    }

    @Override
    protected JobManagerMessage.TaskAssignResponse assignTask(JobManagerMessage.TaskAssignRequest request) throws Exception {
        CrawlSourceAssignRequest assignRequest = (CrawlSourceAssignRequest) request;
        int size = assignRequest.getSize();
        String table = getConfig().getDomain().replace('.', '_');
        if (urls == null) {
            urls = getUrls(table, lastID);
        }

        List<URLInfo> sources = assignSources(size);

        if (sources.size() == 0) {
            urls = getUrls(table, lastID);
            sources = assignSources(size);
        }

        if (sources.size() > 0) {
            if (urls.previous()) {
                lastID = urls.getInt("id");
                log().info("last ID : " + lastID);
            }
            setStatusOfRequestingTaskManagers(getSender(), false);
        } else {
            setStatusOfRequestingTaskManagers(getSender(), true);
            if (isAllTaskManagerWaiting()) {
                return new JobManagerMessage.TaskAssignFinishResponse();
            }
            log().info("send waiting message to job-manager : {}", getSender());
            return new JobManagerMessage.TaskAssignWaitingResponse(getConfig().getRequsetWaitingTimeSec());
        }

        return new CrawlSourceAssignResponse(sources);
    }

    protected void processExtractedUrls(UrlInfos urls) throws SQLException {
        System.out.println(urls);
        for (URLInfo urlInfo : urls.getSources()) {
            db.insert(String.format("INSERT INTO %s (url, depth) SELECT '%s', '%s' FROM DUAL WHERE NOT EXISTS (SELECT * FROM %s WHERE url = '%s')"
                , table, urlInfo.getUrl(), urlInfo.getDepth(), table, urlInfo.getUrl()));
        }
    }

    private void setStatusOfRequestingTaskManagers(ActorRef taskManager, boolean value) {
        requestingTaskManagers.put(taskManager.path().toStringWithoutAddress(), value);
    }

    private boolean isAllTaskManagerWaiting() {
        for (boolean value : requestingTaskManagers.values()) {
            if (!value) return false;
        }
        return true;
    }

    private ResultSet getUrls(String table, int lastID) throws SQLException {
        return db.select(String.format("SELECT id, url, depth FROM %s WHERE id > %d ORDER BY id ASC", table, lastID));
    }

    private List<URLInfo> assignSources(int size) throws SQLException {
        List<URLInfo> sources = new ArrayList<>(size);
        while (urls.next()) {
            sources.add(new URLInfo(domain, urls.getString("url"), urls.getInt("depth")));
            if (sources.size() == size) break;
        }
        return sources;
    }

    private void createGroupsTable() throws SQLException {
        if (!db.exist(GROUPS_TABLE_NAME)) {
            db.create(String.format("CREATE TABLE IF NOT EXISTS %s ("
                + "name varchar (64) not null,"
                + "domain varchar (128) not null,"
                + "reg_date datetime not null default CURRENT_TIMESTAMP,"
                + "primary key (name, domain)"
                + ")", GROUPS_TABLE_NAME));
        }
    }

    private void insertGroup(String group, String domain) throws SQLException {
        db.insert(String.format("INSERT INTO %s (name, domain) SELECT '%s', '%s' FROM DUAL WHERE NOT EXISTS (SELECT * FROM %s WHERE name = '%s' AND domain = '%s')"
            , GROUPS_TABLE_NAME, group, domain, GROUPS_TABLE_NAME, group, domain));
    }

    private void createDomainTable(String table) throws SQLException {
        if (!db.exist(table)) {
            db.create(String.format("CREATE TABLE IF NOT EXISTS %s ("
                + "id bigint not null auto_increment,"
                + "url varchar (1024) not null,"
                + "depth int not null,"
                + "reg_date datetime not null default CURRENT_TIMESTAMP,"
                + "upd_date datetime default null,"
                + "primary key (id)"
                + ")", table));
        }
    }

    private void insertSeed(String table, String seed) throws SQLException {
        int count = db.count(String.format("SELECT count(id) FROM %s", table));
        if (count == 0) {
            db.insert(String.format("INSERT INTO %s(url, depth) VALUES ('%s', %d)", table, URLInfo.normalize(seed, false), 0));
        }
    }

}
