package org.conqueror.lion.db;

import org.conqueror.lion.exceptions.db.DBException;
import org.conqueror.lion.db.table.Table;

public interface Database {

	void open() throws DBException;

	void close() throws DBException;

	void commit() throws DBException;

	boolean hasTable(String name);

	Table openTable(Class<? extends Table> tableClass) throws DBException;

}
