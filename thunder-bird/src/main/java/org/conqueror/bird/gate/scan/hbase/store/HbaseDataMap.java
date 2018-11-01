package org.conqueror.bird.gate.scan.hbase.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NavigableSet;


public class HbaseDataMap {

    private static final Logger logger = LoggerFactory.getLogger(HbaseDataMap.class);

    private static final String FILE_MAP_ROWKEY_KEY = "RowKey";

    public boolean open(String fileName) {
        if (fileName != null) {
        } else {
            logger.error("The hbase data file map don't open (file name is wrong)");
            return false;
        }

        return true;
    }

    public void close() {
    }

    public NavigableSet<String> getTableNames() {
        return null;
    }

    public byte[] getRowKey(String indexName, String tableName) {
//        return tableRowKeyMap.get(indexName + "@" + tableName);
        return null;
    }

    public void putRowKey(String indexName, String tableName, byte[] rowKey) {
//        tableRowKeyMap.put(indexName + "@" + tableName, rowKey);
    }

    public void removeRowKey(String indexName, String tableName) {
//        tableRowKeyMap.remove(indexName + "@" + tableName);
    }
}
