package org.conqueror.lion.job;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

public abstract class JobData implements Serializable {

	public String toRestorableText() throws IOException {
		return new ObjectMapper().writeValueAsString(this);
	}

	public static JobData restore(String restorableText, Class<? extends JobData> jobDataClass) throws IOException {
		return new ObjectMapper().readValue(restorableText, jobDataClass);
	}

}
