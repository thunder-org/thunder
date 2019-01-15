package org.conqueror.bird.gate.scan;

import org.conqueror.bird.exceptions.scan.ScanException;
import org.conqueror.bird.gate.document.Document;
import org.conqueror.bird.gate.source.GateSource;

import java.io.Closeable;
import java.util.List;


public abstract class GateScanner implements Closeable {

    public abstract void open(GateSource source) throws ScanException;

    public abstract void close();

    public abstract List<Document> scan() throws ScanException;

}
