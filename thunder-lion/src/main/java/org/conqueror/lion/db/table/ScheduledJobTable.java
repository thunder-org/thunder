package org.conqueror.lion.db.table;

import org.conqueror.lion.exceptions.db.DBException;
import org.conqueror.lion.schedule.job.JobID;
import org.conqueror.lion.schedule.job.ScheduledJobInfo;
import org.mapdb.DB;
import org.mapdb.Serializer;


public class ScheduledJobTable extends JobTable<JobID, ScheduledJobInfo> {

    public ScheduledJobTable(DB db) {
        super(db);
    }

    public Serializer<JobID> getKeySerializer() {
        return JobID.getSerializer();
    }

    public Serializer<ScheduledJobInfo> getValueSerializer() {
        return ScheduledJobInfo.getSerializer();
    }

    public static ScheduledJobTable newInstance(DB db) throws DBException {
        return JobTable.newInstance(ScheduledJobTable.class, db);
    }

}
