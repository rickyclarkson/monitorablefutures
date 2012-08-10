package com.github.rickyclarkson.monitorablefutures;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MonitorableExecutorService implements Executor {
    private final ExecutorService executorService;

    private MonitorableExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public static MonitorableExecutorService monitorable(ExecutorService executorService) {
        return new MonitorableExecutorService(executorService);
    }

    public <A> MonitorableFuture<A> submit(Monitorable<A> command) {
        final Future<A> wrapped = executorService.submit(command);
        return new MonitorableFuture<A>(wrapped, command.updates);
    }

    @Override
    public void execute(Runnable command) {
        executorService.execute(command);
    }
}
