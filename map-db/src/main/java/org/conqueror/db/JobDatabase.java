package org.conqueror.db;

import org.conqueror.db.table.JobTable;
import org.conqueror.db.table.Table;
import org.conqueror.db.exceptions.db.DBException;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.Closeable;
import java.io.IOException;


@SuppressWarnings("unused")
public class JobDatabase implements Database {

    private DB db;
    private String dbFilePath;
    private long startDBSize;


    public JobDatabase(String dbFilePath) {
        this(dbFilePath, 1024);
    }

    public JobDatabase(String dbFilePath, long startDBSize) {
        this.dbFilePath = dbFilePath;
        this.startDBSize = startDBSize;
    }

    @Override
    public void open() throws DBException {
        try {
            db = DBMaker.fileDB(dbFilePath)
                .allocateStartSize(startDBSize)
                .fileMmapEnable()
                .transactionEnable() // Write Ahead Log
                .make();
        } catch (Exception e) {
            throw new DBException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void close() throws DBException {
        if (db.isClosed()) {
            throw new DBException("db has already been closed");
        } else {
            for (Object table : db.getAll().values()) {
                if (table instanceof Closeable) {
                    try {
                        ((Closeable) table).close();
                    } catch (IOException e) {
                        throw new DBException(e.getMessage(), e.getCause());
                    }
                }
            }
            db.close();
        }
    }

    @Override
    public void commit() {
        db.commit();
    }

    @Override
    public boolean hasTable(String name) {
        return db.exists(name);
    }

    @Override
    public Table openTable(Class<? extends Table> tableClass) throws DBException {
        try {
            //noinspection unchecked
            return JobTable.newInstance((Class<? extends JobTable>) tableClass, db);
        } catch (ClassCastException e) {
            throw new DBException("not job table : " + JobTable.getTableName(tableClass), e.getCause());
        }
    }

}
