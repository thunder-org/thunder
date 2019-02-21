package org.conqueror.drone.data.messages;

import org.conqueror.drone.data.url.URLInfo;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.message.JobManagerMessage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CrawlSourceAssignResponse extends JobManagerMessage.TaskAssignResponse {

    private final List<URLInfo> sources;

    public CrawlSourceAssignResponse() {
        this(null);
    }

    public CrawlSourceAssignResponse(List<URLInfo> sources) {
        this.sources = sources;
    }

    public List<URLInfo> getSources() {
        return sources;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeInt(sources.size());
            if (!sources.isEmpty()) {
                for (URLInfo source : sources) {
                    source.writeObject(output);
                }
            }
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public CrawlSourceAssignResponse readObject(DataInput input) throws SerializableException {
        try {
            int size = input.readInt();
            List<URLInfo> sources = new ArrayList<>(size);
            for (int num = 0; num < size; num++) {
                sources.add(URLInfo.getEmpltyInstance().readObject(input));
            }
            return new CrawlSourceAssignResponse(sources);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CrawlSourceAssignResponse{\n\tsources=[");
        getSources().forEach(source -> sb.append("\n\t\t").append(source));
        sb.append("\t\n]\n}");
        return sb.toString();
    }

}
