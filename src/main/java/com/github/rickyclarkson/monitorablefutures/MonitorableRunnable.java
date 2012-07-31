package com.github.rickyclarkson.monitorablefutures;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class MonitorableRunnable<T> implements Runnable {
    private final BlockingQueue<T> progress = new ArrayBlockingQueue<T>(1);

    public final BlockingQueue<T> updates() {
        return progress;
    }
}
