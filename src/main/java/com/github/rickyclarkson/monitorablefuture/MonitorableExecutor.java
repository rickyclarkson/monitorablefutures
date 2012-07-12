package com.github.rickyclarkson.monitorablefuture;

public interface MonitorableExecutor {
  <Progress, Result> MonitorableFuture<Progress, Result> submit(Monitorable<Progress, Result> callable);
}
