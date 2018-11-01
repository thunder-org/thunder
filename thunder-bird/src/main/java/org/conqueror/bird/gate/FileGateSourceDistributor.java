package org.conqueror.bird.gate;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.exceptions.parse.ParseException;
import org.conqueror.bird.exceptions.schema.SchemaException;
import org.conqueror.bird.gate.parser.JsonParser;
import org.conqueror.bird.gate.parser.Parser;
import org.conqueror.bird.gate.scan.FileGateScanner;
import org.conqueror.bird.gate.scan.file.FileInfoBuilder;
import org.conqueror.bird.gate.scan.file.hdfs.HdfsFileInfoBuilder;
import org.conqueror.bird.gate.scan.file.local.LocalFileInfoBuilder;
import org.conqueror.bird.gate.scan.file.remote.RemoteFileInfoBuilder;
import org.conqueror.bird.gate.source.FileGateSource;
import org.conqueror.bird.gate.source.GateSource;
import org.conqueror.bird.gate.source.schema.DocumentSchema;
import org.conqueror.common.utils.file.FileScanner;
import org.conqueror.common.utils.file.hdfs.HdfsFileScanner;
import org.conqueror.common.utils.file.local.LocalFileScanner;
import org.conqueror.common.utils.file.remote.RemoteFileScanner;

import java.util.*;


public class FileGateSourceDistributor extends GateSourceDistributor {

    private final Queue<GateSource> gateSources = new ArrayDeque<>();

    public FileGateSourceDistributor(ActorSystem system, ActorRef jobManager, IndexConfig config) {
        super(system, jobManager, config);
    }

    @Override
    public void loadGateSources() throws ParseException, SchemaException {
        for (String fileInfo : getConfig().getDestFileList()) {
            List<GateSource> sources = addToGateSources(gateSources, fileInfo);
            getLogger().info("gate-source expression:{}", fileInfo);
            if (sources != null) {
                for (GateSource source : sources) {
                    getLogger().info("\t-> gate-source : {}", source);
                }
            } else {
                getLogger().info("\t-> null");
            }
        }
    }

    @Override
    public List<GateSource> takeGateSources(int size) {
        List<GateSource> sources = new ArrayList<>(size);
        for (int num=0; num<size; num++) {
            if (gateSources.isEmpty()) break;
            sources.add(gateSources.poll());
        }
        return sources;
    }

    private List<GateSource> addToGateSources(Collection<GateSource> gateSources, String fileInfo) throws ParseException, SchemaException {
        String[] segments = fileInfo.split(";");     // [file|...];[json|...];schemaName1,...;mappingName1,...;file://path
        if (segments.length != 5) throw new ParseException("failed to parse a gate source expression : " + fileInfo);

        String type = segments[0];
        String format = segments[1];
        String schemaNameExp = segments[2];
        String mappingNameExp = segments[3];
        String fileUriExp = segments[4];

        List<GateSource> sources = type.equalsIgnoreCase("file") ?
            toFileGateResources(format, schemaNameExp, mappingNameExp, fileUriExp)
            : null;
        if (sources != null) gateSources.addAll(sources);

        return sources;
    }

    private List<GateSource> toFileGateResources(String fileType, String schemaNameExp, String mappingNameExp, String fileUriExp) throws ParseException, SchemaException {
        FileScanner scanner = FileGateScanner.makeFileScanner(fileUriExp);
        if (scanner == null) return null;

        FileInfoBuilder fileInfoBuilder;
        if (scanner instanceof LocalFileScanner) {
            fileInfoBuilder = LocalFileInfoBuilder.getInstance();
        } else if (scanner instanceof RemoteFileScanner) {
            fileInfoBuilder = RemoteFileInfoBuilder.getInstance();
        } else if (scanner instanceof HdfsFileScanner) {
            fileInfoBuilder = HdfsFileInfoBuilder.getInstance();
        } else {
            return null;
        }

        Parser parser = fileType.equalsIgnoreCase("json") ? JsonParser.getInstance() : null;

        String[] schemaNames = schemaNameExp.split(",");
        String[] mappingNames = mappingNameExp.split(",");
        if (schemaNames.length != mappingNames.length) {
            getLogger().error("must be equal to the number of schema-names and mapping-names");
            return null;
        }
        DocumentSchema[] schemas = new DocumentSchema[schemaNames.length];
        for (int schemaIdx = 0; schemaIdx < schemaNames.length; schemaIdx++) {
            schemas[schemaIdx] = DocumentSchema.buildSchema(getConfig().getSchema(schemaNames[schemaIdx])
                , getConfig().getIndexInfo().getMapping(mappingNames[schemaIdx]));

        }

        List<String> fileUris = fileInfoBuilder.toFileUris(fileUriExp);
        List<GateSource> resources = new ArrayList<>(fileUris.size());
        for (String fileUri : fileUris) {
            resources.add(new FileGateSource(fileUri, schemas, parser));
        }

        return resources;
    }

}
