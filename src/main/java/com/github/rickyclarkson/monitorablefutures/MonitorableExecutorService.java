package com.github.rickyclarkson.monitorablefutures;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MonitorableExecutorService {
    private final ExecutorService executorService;

    private MonitorableExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public static MonitorableExecutorService monitorable(ExecutorService executorService) {
        return new MonitorableExecutorService(executorService);
    }

    public <A> MonitorableFuture<Void, A> submit(MonitorableRunnable<A> command) {
        final Future<?> wrapped = executorService.submit(command);
        return new MonitorableFuture<Void, A>(new Future<Void>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return wrapped.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return wrapped.isCancelled();
            }

            @Override
            public boolean isDone() {
                return wrapped.isDone();
            }

            @Override
            public Void get() throws InterruptedException, ExecutionException {
                wrapped.get();
                return null;
            }

            @Override
            public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                wrapped.get(timeout, unit);
                return null;
            }
        }, command.updates());
    }
}
