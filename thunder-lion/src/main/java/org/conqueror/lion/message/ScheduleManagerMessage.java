package org.conqueror.lion.message;

import org.conqueror.lion.message.JobMasterMessage.JobMasterRequest;


public abstract class ScheduleManagerMessage implements LionMessage {

    public static abstract class ScheduleManagerRequest extends ScheduleManagerMessage {

    }

    public static abstract class ScheduleManagerResponse extends ScheduleManagerMessage {

    }

    public static class JobMasterScheduleRequest extends ScheduleManagerRequest {

        private final JobMasterRequest request;

        public JobMasterScheduleRequest(JobMasterRequest request) {
            this.request = request;
        }

    }

}
