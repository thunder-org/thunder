package org.conqueror.lion.id;

import akka.cluster.Cluster;
import akka.cluster.ddata.DistributedData;
import akka.util.Timeout;
import org.conqueror.lion.schedule.store.DDataMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.conqueror.lion.cluster.node.Path.*;


public class IDMaker {

    private final AtomicInteger lastNodeMasterNumber;
    private final AtomicInteger lastNodeWorkerNumber;

    private final AtomicInteger lastNodeWorkerManagerNumbers;
    private final AtomicInteger lastScheduleManagerNumbers;
    private final AtomicInteger lastJobMasterNumbers;
    private final AtomicInteger lastTaskMasterNumbers;

    private final Map<String, Integer> lastJobManagerNumbers = new ConcurrentHashMap<>();
    private final Map<String, Integer> lastTaskManagerNumbers = new ConcurrentHashMap<>();

    private final DDataMap<String, Integer> ids;

    private static final String ID_DELIM = "-";

    public IDMaker(String id, Cluster cluster, Timeout timeout) {
        ids = new DDataMap<>(id, cluster, DistributedData.get(cluster.system()).replicator(), timeout);
        lastNodeMasterNumber = new AtomicInteger(ids.getOrDefault(NODE_MASTER_NAME, 0));

        lastNodeWorkerManagerNumbers = new AtomicInteger(ids.getOrDefault(NODE_WORKER_MANAGER_NAME, 0));
        lastScheduleManagerNumbers = new AtomicInteger(ids.getOrDefault(SCHEDULER_MANAGER_NAME, 0));
        lastJobMasterNumbers = new AtomicInteger(ids.getOrDefault(JOB_MASTER_NAME, 0));

        lastNodeWorkerNumber = new AtomicInteger(ids.getOrDefault(NODE_WORKER_NAME, 0));
        lastTaskMasterNumbers = new AtomicInteger(ids.getOrDefault(TASK_MASTER_NAME, 0));
    }

    /*
        NodeMasterID : NodeMaster-node_master_number
     */
    public String makeNodeMasterID() {
        // can make only one node-master
        return lastNodeMasterNumber.compareAndSet(0, 1) ? NODE_MASTER_NAME + ID_DELIM + 1 : null;
    }

    /*
    NodeWorkerID : NodeWorker-node_worker_number
     */
    public String makeNodeWorkerID() {
        int number = lastNodeWorkerNumber.incrementAndGet();
        ids.put(NODE_WORKER_NAME, number);
        return NODE_WORKER_NAME + ID_DELIM + number;
    }

    /*
    NodeWorkerManagerID : NodeWorkerManager-node_worker_manager_number
     */
    public String makeNodeWorkerManagerID() {
        int number = lastNodeWorkerManagerNumbers.incrementAndGet();
        ids.put(NODE_WORKER_MANAGER_NAME, number);
        return NODE_WORKER_MANAGER_NAME + ID_DELIM + number;
    }

    /*
    ScheduleManagerID : ScheduleManager-schedule_manager_number
     */
    public String makeScheduleManagerID() {
        int number = lastScheduleManagerNumbers.incrementAndGet();
        ids.put(SCHEDULER_MANAGER_NAME, number);
        return SCHEDULER_MANAGER_NAME + ID_DELIM + number;
    }

    /*
    JobMasterID : JobMaster-job_master_number
     */
    public String makeJobMasterID() {
        int number = lastJobMasterNumbers.incrementAndGet();
        ids.put(JOB_MASTER_NAME, number);
        return JOB_MASTER_NAME + ID_DELIM + number;
    }

    /*
    JobManagerID : JobManager-job_master_number-job_manager_number
     */
    public String makeJobManagerID(String jobMasterID) {
        return JOB_MANAGER_NAME + ID_DELIM + getNumber(jobMasterID) + ID_DELIM
            + lastJobManagerNumbers.compute(jobMasterID, (k, y) -> y != null ? y + 1 : 1);
    }

    /*
    JobMasterID : TaskMaster-task_master_number
     */
    public String makeTaskMasterID() {
        int number = lastTaskMasterNumbers.incrementAndGet();
        ids.put(TASK_MASTER_NAME, number);
        return TASK_MASTER_NAME + ID_DELIM + number;
    }

    /*
    JobManagerID : TaskWorker-job_master_number-job_manager_number
     */
    public String makeTaskManagerID(String taskMasterID) {
        return TASK_MANAGER_NAME + ID_DELIM + getNumber(taskMasterID) + ID_DELIM
            + lastTaskManagerNumbers.compute(taskMasterID, (k, y) -> y != null ? y + 1 : 1);
    }

    /*
    return string except Class Name
     */
    private static String getNumber(String id) {
        String[] segments = id.split(ID_DELIM, 2);
        if (segments.length != 2) return null;
        return segments[1];
    }

}
