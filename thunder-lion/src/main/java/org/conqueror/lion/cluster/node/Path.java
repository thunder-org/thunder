package org.conqueror.lion.cluster.node;

public class Path {

    /*
     * node
     */
    public static final String CLUSTER_LISTENER_NAME = "cluster-listener";
    public static final String NODE_MASTER_NAME = "node-master";
    public static final String NODE_WORKER_NAME = "node-worker";
    public static final String SINGLETON_NAME = "active";
    public static final String NODE_MASTER_PROXY = "node-master-proxy";

    /*
     * node-master
     */
	public static final String ID_ISSUER_NAME = "id-issuer";
    public static final String NODE_WORKER_MANAGER_NAME = "node-worker-manager";
    public static final String SCHEDULER_MANAGER_NAME = "scheduler-manager";
    public static final String JOB_MASTER_NAME = "job-master";
    public static final String JOB_MANAGER_NAME = "job-manager";

    /*
     * node-worker
     */
    public static final String TASK_MASTER_NAME = "task-master";
    public static final String TASK_MANAGER_NAME = "task-manager";

    // path
    public static final String CLUSTER_LISTENER_PATH = "/user/" + CLUSTER_LISTENER_NAME;
    public static final String NODE_MASTER_PATH = "/user/" + NODE_MASTER_NAME;
    public static final String NODE_MASTER_SINGLETON_PATH = NODE_MASTER_PATH + "/" + SINGLETON_NAME;
    public static final String ID_ISSUER_PATH = NODE_MASTER_PATH + "/" + ID_ISSUER_NAME;
    public static final String NODE_WORKER_PATH = NODE_MASTER_PATH + "/" + NODE_WORKER_MANAGER_NAME;
    public static final String SCHEDULER_MANAGER_PATH = NODE_MASTER_PATH + "/" + SCHEDULER_MANAGER_NAME;
    public static final String JOB_MASTER_PATH = NODE_MASTER_PATH + "/" + JOB_MASTER_NAME;
    public static final String NODE_WORKERS_PATH = "/user/" + NODE_WORKER_NAME + "*";
    public static final String NODE_MASTER_RECEPTIONIST_PATH = "/system/receptionist";

    public static String getJobManagerPath(String jobManagerID) {
        return NODE_MASTER_PATH + "/" + jobManagerID;
    }

    public static String getNodeWorkerPath(String nodeWorkerID) {
        return "/user/" +  nodeWorkerID;
    }

    public static String getTaskManagerPath(String nodeWorkerID, String taskMasterID) {
        return "/user/" + nodeWorkerID + "/" + taskMasterID;
    }

    public static String getTaskManagerPath(String nodeWorkerID, String taskMasterID, String taskManagerID) {
        return "/user/" + nodeWorkerID + "/" + taskMasterID + "/" + taskManagerID;
    }

}
