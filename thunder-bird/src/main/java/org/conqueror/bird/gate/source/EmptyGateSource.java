package org.conqueror.bird.gate.source;

import org.conqueror.bird.gate.source.schema.DocumentSchema;
import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;


public class EmptyGateSource extends GateSource {

    private static final EmptyGateSource emptyGateSource = new EmptyGateSource();

    private EmptyGateSource() {
        super((DocumentSchema[]) null, null);
    }


    public static EmptyGateSource getInstance() {
        return emptyGateSource;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {

    }

    @Override
    public EmptyGateSource readObject(DataInput input) throws SerializableException {
        return new EmptyGateSource();
    }

}
