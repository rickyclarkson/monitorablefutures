package com.github.rickyclarkson.monitorablefutures;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MonitorableExecutorService {
    private final ExecutorService executorService;

    public MonitorableExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public static MonitorableExecutorService monitorable(ExecutorService executorService) {
        return new MonitorableExecutorService(executorService);
    }

    public <A> MonitorableFuture<?, A> submit(MonitorableRunnable<A> command) {
        final Future<?> wrapped = executorService.submit(command);
        return new MonitorableFuture<Object, A>(new Future<Object>() {
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
            public Object get() throws InterruptedException, ExecutionException {
                return wrapped.get();
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return wrapped.get(timeout, unit);
            }
        }, command.updates());
    }
}
