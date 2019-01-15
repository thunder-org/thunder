package org.conqueror.bird.gate.scan;

import org.conqueror.bird.exceptions.scan.ScanException;
import org.conqueror.bird.exceptions.schema.SchemaException;
import org.conqueror.bird.gate.document.Document;
import org.conqueror.bird.gate.parser.JsonParser;
import org.conqueror.bird.gate.source.FileGateSource;
import org.conqueror.bird.gate.source.GateSource;
import org.conqueror.bird.gate.source.schema.DocumentSchema;
import org.conqueror.common.utils.file.FileUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class FileGateScannerTest {

    @Test
    public void scan() throws SchemaException, ScanException {
        String schema = FileUtils.getFileContent("G:/workspace/thunder/data/conf/schema/estate.json");
        String mapping = FileUtils.getFileContent("G:/workspace/thunder/data/conf/mapping/estate.json");
        DocumentSchema documentSchema = DocumentSchema.buildSchema(schema, mapping);
        FileGateScanner scanner = new FileGateScanner(100);
        GateSource source = new FileGateSource("file:///G:/workspace/thunder/data/sources/estate_data.201601.json", new DocumentSchema[] {documentSchema}, JsonParser.getInstance());
        scanner.open(source);
        List<Document> docs = scanner.scan();
        for (Document doc : docs) {
            System.out.println(doc);
        }
        scanner.close();
    }

}