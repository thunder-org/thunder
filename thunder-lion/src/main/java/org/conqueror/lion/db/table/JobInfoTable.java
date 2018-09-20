package org.conqueror.lion.db.table;

import org.conqueror.lion.exceptions.db.DBException;
import org.conqueror.lion.job.JobID;
import org.conqueror.lion.job.JobInfo;
import org.mapdb.DB;
import org.mapdb.Serializer;

public class JobInfoTable extends JobTable<JobID, JobInfo> {

	public JobInfoTable(DB db) {
		super(db);
	}

	public Serializer<JobID> getKeySerializer() {
		return JobID.getSerializer();
	}

	public Serializer<JobInfo> getValueSerializer() {
		return JobInfo.getSerializer();
	}

	public static JobInfoTable newInstance(DB db) throws DBException {
		return JobTable.newInstance(JobInfoTable.class, db);
	}

}
