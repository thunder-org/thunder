package org.conqueror.lion.message;

public class TaskManagerMessage {

    public static abstract class TaskManagerRequest extends TaskManagerMessage {

    }

    public static abstract class TaskManagerResponse extends TaskManagerMessage {

    }

    public static final class TaskWorkerFinishRequest extends TaskManagerRequest {

    }

}
