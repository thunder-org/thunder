package org.conqueror.common.utils.db;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;


public class DBConnectorTest {

    private DB db;
    private DBConfigurationBuilder config;

    private static final String DB_NAME = "test_db"; // or just "test"
    private static final String TABLE_NAME = "test_table"; // or just "test"

    @Before
    public void setUp() throws Exception {
        config = DBConfigurationBuilder.newBuilder();
        config.setPort(0); // 0 => autom. detect free port
        db = DB.newEmbeddedDB(config.build());
        db.start();
        db.createDB(DB_NAME);
    }

    @Test
    public void test() throws ManagedProcessException, SQLException {
        DBConnector connector = new DBConnector(config.getURL(DB_NAME));
        Assert.assertFalse(
            connector.create(String.format("CREATE TABLE IF NOT EXISTS %s ("
                + "name varchar (64) not null,"
                + "domain varchar (128) not null,"
                + "reg_date datetime not null default CURRENT_TIMESTAMP,"
                + "primary key (name, domain)"
                + ")", TABLE_NAME))
        );
        Assert.assertTrue(
            connector.exist(TABLE_NAME)
        );
        Assert.assertEquals(1
            , connector.insert(String.format("INSERT INTO %s (name, domain) VALUES ('test1', 'www.test.com')", TABLE_NAME))
        );
        Assert.assertEquals(1
            , connector.count(String.format("SELECT count(*) from %s", TABLE_NAME))
        );
        ResultSet result = connector.select(String.format("SELECT name, domain from %s", TABLE_NAME));
        Assert.assertTrue(result.next());
        Assert.assertEquals("test1"
            , result.getString("name")
        );
        Assert.assertEquals("www.test.com"
            , result.getString("domain")
        );
    }

    @After
    public void tearDown() throws Exception {
        db.stop();
    }
}