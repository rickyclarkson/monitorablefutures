package com.github.rickyclarkson.monitorablefutures;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MonitorableFuture<A> {
    private final Future<A> future;
    private final BlockingQueue<A> updates;

    public MonitorableFuture(Future<A> future, BlockingQueue<A> updates) {
        this.future = future;
        this.updates = updates;
    }

    public BlockingQueue<A> updates() {
        return updates;
    }

    public boolean isDone() {
        return future.isDone();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }

    public A get() throws ExecutionException, InterruptedException {
        return future.get();
    }

    public A get(int timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }
}
