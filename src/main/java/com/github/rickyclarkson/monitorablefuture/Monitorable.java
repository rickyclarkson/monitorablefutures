package com.github.rickyclarkson.monitorablefuture;

import java.util.concurrent.Callable;

public interface Monitorable<Progress, Result> extends Callable<Result> {
  Progress progress();
}
