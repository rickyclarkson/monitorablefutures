package com.github.rickyclarkson.monitorablefutures;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.github.rickyclarkson.monitorablefutures.MonitorableExecutorService.monitorable;

public class MonitorableFuturesTest {
    @Test
    public void monitoringWithStandardFramework() throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        class Sleeping implements Runnable {
            final BlockingQueue<Integer> progress = new ArrayBlockingQueue<Integer>(2);
            @Override
            public void run() {
                for (int a = 0; a < 3; a++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    progress.add(a);
                }
            }
        }
        Sleeping sleeping = new Sleeping();
        Future<?> future = service.submit(sleeping);
        while (!future.isDone()) {
            Thread.sleep(100);
            final Integer integer = sleeping.progress.poll(10, TimeUnit.MILLISECONDS);
            if (integer != null)
                System.out.println("Progress: " + integer);
        }
    }

    @Test
    public void withMonitorable() throws InterruptedException {
        MonitorableExecutorService service = monitorable(Executors.newSingleThreadExecutor());
        class Sleeping extends MonitorableRunnable<Integer> {
            @Override
            public void run() {
                for (int a = 0; a < 3; a++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    updates().add(a);
                }
            }
        }
        MonitorableFuture<?, Integer> future = service.submit(new Sleeping());
        while (!future.isDone()) {
            Thread.sleep(100);
            final Integer integer = future.updates().poll(10, TimeUnit.MILLISECONDS);
            if (integer != null)
                System.out.println("Progress: " + integer);
        }
    }
}
