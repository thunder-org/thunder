package org.conqueror.lion.job;

public class TestJob extends Job {

	public TestJob(TestJobConfig config) {
		super(config);
	}

	public String getValue() {
		return ((TestJobConfig) config).getValue();
	}

}
