package com.github.rickyclarkson.monitorablefutures;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class Monitorable<T> {
    public final BlockingQueue<T> updates;

    public Monitorable(BlockingQueue<T> updates) {
        this.updates = updates;
    }

    public Monitorable() {
        updates = new ArrayBlockingQueue<T>(1);
    }

    public abstract T call(MonitorableExecutorService service);
}
