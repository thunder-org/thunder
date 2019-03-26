package org.conqueror.bird.gate.scan;

import org.conqueror.bird.exceptions.scan.ScanException;
import org.conqueror.bird.gate.document.Document;
import org.conqueror.bird.gate.document.DocumentBuilder;
import org.conqueror.bird.gate.parser.Parser;
import org.conqueror.bird.gate.source.FileGateSource;
import org.conqueror.bird.gate.source.GateSource;
import org.conqueror.bird.gate.source.schema.DocumentSchema;
import org.conqueror.common.utils.file.FileScanner;
import org.conqueror.common.utils.file.hdfs.HdfsFileScanner;
import org.conqueror.common.utils.file.local.LocalFileScanner;
import org.conqueror.common.utils.file.remote.RemoteFileScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


public class FileGateScanner extends GateScanner {

    private final DocumentBuilder documentBuilder;

    private FileGateSource source = null;

    private FileScanner scanner = null;
    private BufferedReader reader = null;

    public FileGateScanner(int maxIndexNameSize) {
        documentBuilder = new DocumentBuilder(maxIndexNameSize);
    }

    @Override
    public void open(GateSource source) throws ScanException {
        if (source instanceof FileGateSource) {
            this.source = (FileGateSource) source;
            String fileUri = ((FileGateSource) source).getFileUri();
            scanner = makeFileScanner(fileUri);
            if (scanner != null) {
                try {
                    reader = scanner.getReader(scanner.makeFileInfo(new URI(fileUri)));
                } catch (InterruptedException | IOException | URISyntaxException e) {
                    throw new ScanException(e);
                }
            }
        } else {
            throw new ScanException("gate-source is not file-gate-source");
        }
    }

    @Override
    public void close() {
        try {
            scanner.close();
        } finally {
            reader = null;
        }
    }

    @Override
    public List<Document> scan() throws ScanException {
        if (source != null && reader != null) {
            return scan(source, reader);
        }
        return Collections.emptyList();
    }

    private List<Document> scan(FileGateSource source, BufferedReader reader) throws ScanException {
        List<Document> docs = new ArrayList<>(source.getSchemas().length);
        Parser parser = source.getParser();

        try {
            Collection<Map<String, Object>> data;
            while (!(data = parser.parse(reader)).isEmpty()) {
                for (Map<String, Object> datum : data) {
                    for (DocumentSchema schema : source.getSchemas()) {
                        docs.add(documentBuilder.buildDocument(schema, datum));
                    }
                }
            }
        } catch (Exception e) {
            throw new ScanException("failed to scan - file gate scanner : " + source + "\n\t" + e.getMessage(), e.getCause());
        }

        return docs;
    }

    public static FileScanner makeFileScanner(String fileUri) {
        String scheme;
        try {
            URI uri = new URI(fileUri);
            scheme = uri.getScheme();
        } catch (URISyntaxException e) {
            int eidx = fileUri.indexOf(':');
            if (eidx != -1) {
                scheme = fileUri.substring(0, eidx);
            } else {
                return null;
            }
        }

        if (scheme != null) {
            switch (scheme) {
                case "file":
                    return new LocalFileScanner();
                case "sftp":
                    return new RemoteFileScanner(3);
                case "hdfs":
                    return new HdfsFileScanner();
                default:
                    return null;
            }
        }

        return null;
    }

}
