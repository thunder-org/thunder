package org.conqueror.bird.gate.source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class GateSourceAccessor {

    private final List<GateSource> allocatedSources = new ArrayList<>();
    private final List<GateSource> completedSources = new ArrayList<>();
    private final List<GateSource> failedSources = new ArrayList<>();

    private final BlockingQueue<GateSource> waitingSources;

    public GateSourceAccessor(int sourceBufferSize) {
        waitingSources = new ArrayBlockingQueue<>(sourceBufferSize);
    }

    public boolean put(GateSource source) {
        if (waitingSources.remainingCapacity() == 0) return false;

        try {
            waitingSources.put(source);
            if (!source.isOver()) {
                allocatedSources.add(source);
            }
        } catch (InterruptedException e) {
            return false;
        }

        return true;
    }

    public boolean put(Collection<GateSource> sources) {
        for (GateSource source : sources) {
            if (!put(source)) return false;
        }

        return true;
    }

    public boolean remainSource() {
        return !waitingSources.isEmpty();
    }

    public GateSource take() {
        try {
            return waitingSources.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void completed(GateSource source, boolean success) {
        if (success) completedSources.add(source);
        else failedSources.add(source);
    }

    public List<GateSource> getAllocatedSources() {
        return allocatedSources;
    }

    public List<GateSource> getCompletedSources() {
        return completedSources;
    }

    public List<GateSource> getFailedSources() {
        return failedSources;
    }

}
