package org.conqueror.lion.schedule.job;

import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;
import org.jetbrains.annotations.NotNull;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class JobID implements LionSerializable<JobID>, Comparable<JobID> {

	private static final Integer NOT_A_JOB_ID = -1;

	private static final JobID EmptyJobID = new JobID(NOT_A_JOB_ID);

	private Integer jobID;

	public JobID(Integer jobID) {
		this.jobID = jobID;
	}

	public Integer getJobID() {
		return jobID;
	}

	public boolean equals(Integer jobID) {

		return this.jobID.equals(jobID);
	}

	public boolean equals(JobID jobID) {
		return equals(jobID.getJobID());
	}

	@Override
	public int hashCode() {
		return jobID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.getClass() == obj.getClass() && hashCode() == obj.hashCode();
	}

	@Override
	public String toString() {
		return String.valueOf(jobID);
	}

	public static boolean isValidJobID(JobID jobID) {
		return jobID != null && !JobID.NOT_A_JOB_ID.equals(jobID.getJobID());
	}

	@Override
	public int compareTo(@NotNull JobID jobID) {
		return this.jobID - jobID.getJobID();
	}

	@Override
	public void writeObject(DataOutput output) throws SerializableException {
		try {
			output.writeInt(getJobID());
		} catch (IOException e) {
			throw new SerializableException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public JobID readObject(DataInput input) throws SerializableException {
		try {
			return new JobID(input.readInt());
		} catch (IOException e) {
			throw new SerializableException(e.getMessage(), e.getCause());
		}
	}

	public static Serializer<JobID> getSerializer() {
		return LionSerializable.getSerializer(JobID.class);
	}

	public static JobID getEmptyInstance() {
		return EmptyJobID;
	}

}