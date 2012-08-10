package com.github.rickyclarkson.monitorablefutures;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public abstract class Monitorable<T> implements Callable<T> {
    public final BlockingQueue<T> updates;

    public Monitorable(BlockingQueue<T> updates) {
        this.updates = updates;
    }

    public Monitorable() {
        updates = new ArrayBlockingQueue<T>(1);
    }
}
