package com.alexkasko.unsafe.offheapstruct;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * User: alexkasko
 * Date: 11/14/13
 */
class LimitedInvoker {
    private final Executor executor;
    private final int maxThreads;

    LimitedInvoker(Executor executor, int maxThreads) {
        if (null == executor) throw new NullPointerException("executor");
        if (maxThreads <= 0) throw new IllegalArgumentException("maxThreads: [" + maxThreads + "] must be positive");
        this.executor = executor;
        this.maxThreads = maxThreads;
    }

    void invokeAll(Collection<? extends Runnable> tasks) {
        ExecutorCompletionService<Void> completer = new ExecutorCompletionService<Void>(executor);
        Iterator<? extends Runnable> iter = tasks.iterator();
        int realMaxThreads = Math.min(maxThreads, tasks.size());
        for (int i = 0; i < realMaxThreads; i++) {
            completer.submit(iter.next(), null);
        }
        for (int i = realMaxThreads; i > 0; ) {
            await(completer);
            i -= 1;
            if (iter.hasNext()) {
                completer.submit(iter.next(), null);
                i += 1;
            }
        }
    }

    private void await(ExecutorCompletionService<Void> completer) {
        try {
            Future<Void> fu = completer.take();
            fu.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
