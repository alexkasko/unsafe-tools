/*
 * Copyright 2014 Alex Kasko (alexkasko.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexkasko.unsafe.offheapstruct;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * Auxiliary class that limits the number of {@link java.util.concurrent.Executor} threads
 * those can be used simultaneously.
 *
 * @author alexkasko
 * Date: 11/14/13
 */
class LimitedInvoker {
    private final Executor executor;
    private final int maxThreads;

    /**
     * Constructor
     *
     * @param executor executor
     * @param maxThreads maximum number of simultaneously used threads
     */
    LimitedInvoker(Executor executor, int maxThreads) {
        if (null == executor) throw new NullPointerException("executor");
        if (maxThreads <= 0) throw new IllegalArgumentException("maxThreads: [" + maxThreads + "] must be positive");
        this.executor = executor;
        this.maxThreads = maxThreads;
    }

    /**
     * Executes all the tasks simultaneously using no more than {@link #maxThreads} threads from the executor
     *
     * @param tasks runnables to execute
     */
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

    /**
     * Waits for {@link java.util.concurrent.Future} to complete
     *
     * @param completer executor service
     */
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
