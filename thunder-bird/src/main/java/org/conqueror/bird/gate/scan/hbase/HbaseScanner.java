package org.conqueror.bird.gate.scan.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;


public class HbaseScanner {

    private static final Logger logger = LoggerFactory.getLogger(HbaseScanner.class);

    private final Connection connection;
    private volatile Table table = null;
    private volatile ResultScanner scanner = null;
    private Iterator<Result> resultIt = null;
    private byte[] lastRowKey = null;
    private final Boolean openedTableLock = true;
    private final Boolean openedScannerLock = true;
    private volatile Boolean isOpenedTable = false;
    private volatile Boolean isOpenedScanner = false;

    public HbaseScanner() throws IOException {
        this(HBaseConfiguration.create());
    }

    public HbaseScanner(Configuration config) throws IOException {
        connection = ConnectionFactory.createConnection(config);
    }

    public boolean open(String tableName, byte[] startRowKey) {
        return openTable(tableName) && openScanner(startRowKey, 0);
    }

    public boolean open(String tableName, byte[] startRowKey, int cacheSize) {
        return openTable(tableName) && openScanner(startRowKey, cacheSize);
    }

    public void close() {
        closeScanner();
        closeTable();
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean openTable(String tableName) {
        try {
            synchronized (openedTableLock) {
                if (!isOpenedTable) {
                    table = connection.getTable(TableName.valueOf(tableName));
                    isOpenedTable = true;
                } else {
                    logger.error("A opened table already exist");
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void closeTable() {
        synchronized (openedTableLock) {
            if (isOpenedTable) {
                try {
                    table.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isOpenedTable = false;
            }
        }
    }

    public boolean openScanner() {
        return openScanner(null, 0);
    }

    public boolean openScanner(byte[] startRowKey, int cacheSize) {
        Scan scan = new Scan();
        if (startRowKey != null) scan.withStartRow(startRowKey);
        if (cacheSize > 0) scan.setCaching(cacheSize);
        logger.debug("scanning caching : {}", cacheSize);

        try {
            synchronized (openedScannerLock) {
                if (!isOpenedScanner) {
                    scanner = table.getScanner(scan);
                    resultIt = scanner.iterator();
                    isOpenedScanner = true;
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean hasNextResult() {
        return resultIt != null && resultIt.hasNext();
    }

    public Result nextResult() {
        if (resultIt != null) {
            Result result = resultIt.next();
            lastRowKey = result.getRow();
            return result;
        }
        return null;
    }

    public byte[] getLastRowKey() {
        return lastRowKey;
    }

    public void closeScanner() {
        synchronized (openedScannerLock) {
            if (isOpenedScanner) {
                try {
                    scanner.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isOpenedScanner = false;
            }
        }
    }
}
