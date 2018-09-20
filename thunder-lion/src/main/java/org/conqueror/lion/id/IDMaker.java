package org.conqueror.lion.id;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.conqueror.lion.cluster.node.Path.*;


public class IDMaker {

    private final AtomicInteger lastNodeMasterNumber = new AtomicInteger(0);
    private final AtomicInteger lastNodeWorkerNumber = new AtomicInteger(0);

    private final AtomicInteger lastNodeWorkerManagerNumbers = new AtomicInteger(0);
    private final AtomicInteger lastScheduleManagerNumbers = new AtomicInteger(0);
    private final AtomicInteger lastJobMasterNumbers = new AtomicInteger(0);
    private final Map<String, Integer> lastJobManagerNumbers = new ConcurrentHashMap<>();
    private final AtomicInteger lastTaskMasterNumbers = new AtomicInteger(0);
    private final Map<String, Integer> lastTaskManagerNumbers = new ConcurrentHashMap<>();

    private static final String ID_DELIM = "-";

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
        return NODE_WORKER_NAME + ID_DELIM + lastNodeWorkerNumber.incrementAndGet();
    }

    /*
    NodeWorkerManagerID : NodeWorkerManager-node_worker_manager_number
     */
    public String makeNodeWorkerManagerID() {
        return NODE_WORKER_MANAGER_NAME + ID_DELIM + lastNodeWorkerManagerNumbers.incrementAndGet();
    }

    /*
    ScheduleManagerID : ScheduleManager-schedule_manager_number
     */
    public String makeScheduleManagerID() {
        return SCHEDULER_MANAGER_NAME + ID_DELIM + lastScheduleManagerNumbers.incrementAndGet();
    }

    /*
    JobMasterID : JobMaster-job_master_number
     */
    public String makeJobMasterID() {
        return JOB_MASTER_NAME + ID_DELIM + lastJobMasterNumbers.incrementAndGet();
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
        return TASK_MASTER_NAME + ID_DELIM + lastTaskMasterNumbers.incrementAndGet();
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
