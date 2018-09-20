package org.conqueror.lion.db;

import org.conqueror.lion.exceptions.db.DBException;
import org.conqueror.lion.db.table.JobTable;
import org.mapdb.DB;
import org.mapdb.Serializer;

public class TestTable extends JobTable<TestTableKey, TestTableValue> {

	public TestTable(DB db) {
		super(db);
	}

	public Serializer<TestTableKey> getKeySerializer() {
		return TestTableKey.getSerializer();
	}

	public Serializer<TestTableValue> getValueSerializer() {
		return TestTableValue.getSerializer();
	}

	public static TestTable newInstance(DB db) throws DBException {
		return JobTable.newInstance(TestTable.class, db);
	}

}
