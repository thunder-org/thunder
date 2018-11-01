package org.conqueror.bird.gate.document;

public class EmptyDocument extends Document {

    private static final EmptyDocument EmptyDocument = new EmptyDocument();

    private EmptyDocument() {
        super(null, null, null);
    }

    public static EmptyDocument getInstance() {
        return EmptyDocument;
    }

}
