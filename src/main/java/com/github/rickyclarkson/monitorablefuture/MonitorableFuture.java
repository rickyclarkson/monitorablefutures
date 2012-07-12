package com.github.rickyclarkson.monitorablefuture;

import java.util.concurrent.Future;

public interface MonitorableFuture<Progress, Result> extends Future<Result> {
  Progress progress();
}
