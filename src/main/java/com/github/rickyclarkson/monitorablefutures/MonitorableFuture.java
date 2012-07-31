package com.github.rickyclarkson.monitorablefutures;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

public class MonitorableFuture<A, B> {
    private final Future<A> future;
    private final BlockingQueue<B> updates;

    public MonitorableFuture(Future<A> future, BlockingQueue<B> updates) {
        this.future = future;
        this.updates = updates;
    }

    public Future<A> future() {
        return future;
    }

    public BlockingQueue<B> updates() {
        return updates;
    }

    public boolean isDone() {
        return future.isDone();
    }
}
