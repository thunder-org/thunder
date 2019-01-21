package org.conqueror.bird.index.source;

import java.util.*;


public class IndexContentQueueMap {

    private final Map<String, IndexContentQueue> queueMap = new HashMap<>();    // key : index name
    private final int capacity;

    public IndexContentQueueMap(int capacity) {
        this.capacity = capacity;
    }

    public IndexContentQueue getQueue(String index) {
        return queueMap.get(index);
    }

    public IndexContentQueue getOrCreateQueue(String index, String mappingName) {
        IndexContentQueue queue;
        queue = queueMap.get(index);
        if (queue == null) {
            queue = new IndexContentQueue(index, mappingName, capacity);
            queueMap.put(index, queue);
        }

        return queue;
    }

    public Set<String> getIndexNames() {
        return new HashSet<>(queueMap.keySet());
    }

    public IndexContentQueue remove(String index) {
        if (queueMap.containsKey(index)) {
            return queueMap.remove(index);
        }
        return null;
    }

    public List<String> getRandomOrderedIndexNames() {
        List<String> indexNames = new ArrayList<>(getIndexNames());
        Collections.shuffle(indexNames);
        return indexNames;
    }

    public boolean remains() {
        return queueMap.size() > 0;
    }

    public void clear() {
        for (IndexContentQueue queue : queueMap.values()) {
            queue.clear();
        }
        queueMap.clear();
    }

}
