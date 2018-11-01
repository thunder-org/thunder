package org.conqueror.bird.data.messages.job.manager;

import org.conqueror.bird.gate.source.GateSource;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.message.JobManagerMessage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class IndexSourceAssignResponse extends JobManagerMessage.TaskAssignResponse {

    private final List<GateSource> sources;

    public IndexSourceAssignResponse() {
        this(null);
    }

    public IndexSourceAssignResponse(List<GateSource> sources) {
        this.sources = sources;
    }

    public List<GateSource> getSources() {
        return sources;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeInt(sources.size());
            if (!sources.isEmpty()) {
                output.writeUTF(sources.get(0).getClass().getName());
                for (GateSource source : sources) {
                    source.writeObject(output);
                }
            }
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public IndexSourceAssignResponse readObject(DataInput input) throws SerializableException {
        try {
            int size = input.readInt();
            List<GateSource> sources = new ArrayList<>(size);
            if (size > 0) {
                GateSource source = (GateSource) Class.forName(input.readUTF()).newInstance();
                for (int num = 0; num < size; num++) {
                    source = (GateSource) source.readObject(input);
                    sources.add(source);
                }
            }
            return new IndexSourceAssignResponse(sources);
        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("IndexSourceAssignResponse{\n\tsources=[");
        getSources().forEach(source -> sb.append("\n\t\t").append(source));
        sb.append("\t\n]\n}");
        return sb.toString();
    }
}
