package com.github.rickyclarkson.monitorablefutures;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.rickyclarkson.monitorablefutures.MonitorableExecutorService.monitorable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MonitorableFuturesTest {
    @Test
    public void withMonitorable() throws InterruptedException {
        MonitorableExecutorService service = monitorable(Executors.newSingleThreadExecutor());
        class Count extends Monitorable<Integer> {
            @Override
            public Integer call(MonitorableExecutorService executorService) {
                for (int a = 0; a < 3; a++) {
                    try {
                        if (!updates.offer(a, 1, TimeUnit.SECONDS))
                            new IllegalStateException("Couldn't offer " + a + " to the BlockingQueue after waiting for 1 second.").printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Added " + a);
                }
                return 3;
            }
        }
        MonitorableFuture<Integer> future = service.submit(new Count());

        for (;;) {
            final Integer integer = future.updates().poll(10, TimeUnit.MILLISECONDS);
            if (integer != null)
                System.out.println("Progress: " + integer);
            if (integer == null)
                continue;

            if (integer == 2) {
                Thread.sleep(500);
                assertTrue(future.isDone());
                return;
            }
            if (integer > 2) {
                fail("The test case is faulty if the value is >2");
            }
        }
    }

    @Test
    public void delegationHappens() {
        final MonitorableExecutorService monitorableExecutorService = MonitorableExecutorService.monitorable(Executors.newFixedThreadPool(1));
        MonitorableFuture<Void> sleepAWhile = monitorableExecutorService.submit(new Monitorable<Void>() {
            @Override
            public Void call(MonitorableExecutorService executorService) {
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        });

        assertFalse(sleepAWhile.isDone());
        assertTrue(sleepAWhile.cancel(true));
        assertTrue(sleepAWhile.isCancelled());

        ExecutorService doNow = new ExecutorService() {
            @Override
            public void shutdown() {
            }

            @Override
            public List<Runnable> shutdownNow() {
                return Collections.emptyList();
            }

            @Override
            public boolean isShutdown() {
                return false;
            }

            @Override
            public boolean isTerminated() {
                return false;
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                return false;
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> Future<T> submit(Runnable task, T result) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Future<?> submit(Runnable task) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };

        final boolean[] pass = {false};

        monitorable(doNow).execute(new Runnable() {
            @Override
            public void run() {
                pass[0] = true;
            }
        });

        assertTrue(pass[0]);
    }

    @Test
    public void getStillWorks() throws ExecutionException, InterruptedException, TimeoutException {
        MonitorableFuture<Void> future = MonitorableExecutorService.monitorable(Executors.newFixedThreadPool(1)).submit(new Monitorable<Void>() {
            @Override
            public Void call(MonitorableExecutorService executorService) {
                return null;
            }
        });

        assertNull(future.get());
        assertNull(future.get(1, TimeUnit.SECONDS));
    }
}
