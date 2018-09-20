package org.conqueror.lion.message;

import org.conqueror.lion.config.JobConfig;


public abstract class JobMasterMessage implements LionMessage {


    public static abstract class JobMasterRequest extends JobMasterMessage {

    }

    public static abstract class JobMasterResponse extends JobMasterMessage {

    }

    public static class JobManagerCreateRequest extends JobMasterRequest {

        private final JobConfig config;

        public JobManagerCreateRequest(JobConfig config) {
            this.config = config;
        }

        public JobConfig getConfig() {
            return config;
        }

    }

    public static class JobManagerCreateResponse extends JobMasterResponse {

    }

    public static final class JobManagerFinishRequest extends JobMasterRequest {

        private final String jobName;

        public JobManagerFinishRequest(String jobName) {
            this.jobName = jobName;
        }

        public String getJobName() {
            return jobName;
        }

    }

}
