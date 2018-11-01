package org.conqueror.bird.gate.scan;

import org.conqueror.bird.exceptions.scan.ScanException;
import org.conqueror.bird.gate.document.Document;
import org.conqueror.bird.gate.document.DocumentBuilder;
import org.conqueror.bird.gate.parser.Parser;
import org.conqueror.bird.gate.source.FileGateSource;
import org.conqueror.bird.gate.source.GateSource;
import org.conqueror.bird.gate.source.GateSourceAccessor;
import org.conqueror.bird.gate.source.schema.DocumentSchema;
import org.conqueror.common.utils.file.FileInfo;
import org.conqueror.common.utils.file.FileScanner;
import org.conqueror.common.utils.file.hdfs.HdfsFileScanner;
import org.conqueror.common.utils.file.local.LocalFileScanner;
import org.conqueror.common.utils.file.remote.RemoteFileScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class FileGateScanner extends GateScanner {

    public final DocumentBuilder documentBuilder;

    public FileGateScanner(GateSourceAccessor sourceAccessor, BlockingQueue<Document> documentQueue, int maxIndexNameSize) {
        super(sourceAccessor, documentQueue);
        documentBuilder = new DocumentBuilder(maxIndexNameSize);
    }

    @Override
    public boolean processSource(GateSource source) {
        if (source instanceof FileGateSource) {
            return processSource((FileGateSource) source);
        } else {
            return false;
        }
    }

    public boolean processSource(FileGateSource source) {
        String fileUri = source.getFileUri();
        int numberOfDocs = 0;
        AtomicInteger numberOfFilteredOut = new AtomicInteger(0);

        FileScanner scanner = makeFileScanner(fileUri);
        if (scanner == null) return false;

        FileInfo fileInfo = null;
        try {
            fileInfo = scanner.makeFileInfo(fileUri);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (fileInfo == null) return false;

        boolean success = true;
        BufferedReader reader = null;
        try {
            reader = scanner.getReader(fileInfo);
            Document[] docs;
            while ((docs = scan(source, reader, numberOfFilteredOut)) != null) {
                put(docs);
                numberOfDocs += docs.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            if (reader != null) try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            scanner.close();
        }
//		stats.addGateStats(source, new GateStats(source.toString(), 1, (success? 0 : 1)
//				, numberOfDocs + numberOfFilteredOut.get(), numberOfFilteredOut.get()));

        return success;
    }

    public Document[] scan(FileGateSource source, BufferedReader reader, AtomicInteger numberOfFilteredOut) throws ScanException {
        Parser parser = source.getParser();
        try {
            while (true) {
                Map<String, Object> data = parser.parse(reader);
                if (data == null) break;

                Document[] docs = new Document[source.getSchemas().length];
                int docIdx = 0, numberOfNotNullDocs = 0;
                for (DocumentSchema schema : source.getSchemas()) {
                    Document doc = documentBuilder.buildDocument(schema, data);
                    docs[docIdx++] = doc;

                    if (doc != null) numberOfNotNullDocs++;
                    else numberOfFilteredOut.incrementAndGet();
                }
                if (numberOfNotNullDocs > 0) return docs;
            }
        } catch (Exception e) {
            throw new ScanException("failed to scan - file gate scanner : " + source, e.getCause());
        }

        return null;
    }

    public static FileScanner makeFileScanner(String fileUri) {
        String scheme;
        try {
            URI uri = new URI(fileUri);
            scheme = uri.getScheme();
        } catch (URISyntaxException e) {
            int eidx = fileUri.indexOf(":");
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
                    return new RemoteFileScanner();
                case "hdfs":
                    return new HdfsFileScanner();
            }
        }

        return null;
    }

}
