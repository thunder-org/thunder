package org.conqueror.bird.index;

import org.conqueror.bird.data.BirdData;
import org.conqueror.common.exceptions.serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IndexInformation implements BirdData {

    private String parentMappingName = null;
    private String childMappingName = null;
    private Map<String, String> mappings = new HashMap<>();
    private int pingTimeOutSec;
    private int nodeSamplerIntervalSec;
    private int requestRetries;
    private String[] addresses;
    private String cluster;

    public String getParentMappingName() {
        return parentMappingName;
    }

    public void setParentMappingName(String parentMappingName) {
        this.parentMappingName = parentMappingName;
    }

    public String getChildMappingName() {
        return childMappingName;
    }

    public void setChildMappingName(String childMappingName) {
        this.childMappingName = childMappingName;
    }

    public String getMapping(String mappingName) {
        return mappings.get(mappingName);
    }

    public void addMappingJson(String mappingName, String mappingJson) {
        mappings.put(mappingName, mappingJson);
    }

    public int getPingTimeOutSec() {
        return pingTimeOutSec;
    }

    public void setPingTimeOutSec(int pingTimeOutSec) {
        this.pingTimeOutSec = pingTimeOutSec;
    }

    public int getNodeSamplerIntervalSec() {
        return nodeSamplerIntervalSec;
    }

    public void setNodeSamplerIntervalSec(int nodeSamplerIntervalSec) {
        this.nodeSamplerIntervalSec = nodeSamplerIntervalSec;
    }

    public int getRequestRetries() {
        return requestRetries;
    }

    public void setRequestRetries(int requestRetries) {
        this.requestRetries = requestRetries;
    }

    public String[] getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses.toArray(new String[0]);
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(parentMappingName);
            output.writeUTF(childMappingName);
            output.writeInt(mappings.size());
            for (Map.Entry<String, String> entry : mappings.entrySet()) {
		        output.writeUTF(entry.getKey());
                output.writeUTF(entry.getValue());
            }
            output.writeInt(pingTimeOutSec);
            output.writeInt(nodeSamplerIntervalSec);
            output.writeInt(requestRetries);
            output.writeInt(addresses.length);
            for (String address : addresses) {
                output.writeUTF(address);
            }
            output.writeUTF(cluster);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public IndexInformation readObject(DataInput input) throws SerializableException {
        try {
            IndexInformation info = new IndexInformation();
            info.setParentMappingName(input.readUTF());
            info.setChildMappingName(input.readUTF());
            for (int idx=0; idx<input.readInt(); idx++) {
                info.addMappingJson(input.readUTF(), input.readUTF());
            }
            info.setPingTimeOutSec(input.readInt());
            info.setNodeSamplerIntervalSec(input.readInt());
            info.setRequestRetries(input.readInt());
            String[] addresses = new String[input.readInt()];
            for (int idx=0; idx<addresses.length; idx++) {
                addresses[idx] = input.readUTF();
            }
            info.setAddresses(addresses);
            info.setCluster(input.readUTF());

            return info;
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }
}
