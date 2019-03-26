package org.conqueror.db;

import org.conqueror.db.exceptions.db.DBException;
import org.conqueror.db.table.Table;

public interface Database {

	void open() throws DBException;

	void close() throws DBException;

	void commit() throws DBException;

	boolean hasTable(String name);

	Table openTable(Class<? extends Table> tableClass) throws DBException;

}
