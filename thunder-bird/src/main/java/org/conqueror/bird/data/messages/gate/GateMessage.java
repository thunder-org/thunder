package org.conqueror.bird.data.messages.gate;

import org.conqueror.bird.data.messages.BirdMessage;
import org.conqueror.bird.gate.source.GateSource;
import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class GateMessage implements BirdMessage {

    public static abstract class GateRequest extends GateMessage {

    }

    public static abstract class GateResponse extends GateMessage {

    }

    public static final class GateTakeSourceResponse extends GateResponse {

        private final GateSource source;

        public GateTakeSourceResponse(GateSource source) {
            this.source = source;
        }

        public GateSource getSource() {
            return source;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(source.getClass().getName());
                source.writeObject(output);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public GateTakeSourceResponse readObject(DataInput input) throws SerializableException {
            try {
                GateSource source = (GateSource) Class.forName(input.readUTF()).newInstance();
                return new GateTakeSourceResponse((GateSource) source.readObject(input));
            } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                throw new SerializableException(e);
            }
        }

    }

}
