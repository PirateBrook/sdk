package com.piratebrook.sdk.util;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

import android.os.Process;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class SystemServerInitThreadPool {
    private static final String TAG = SystemServerInitThreadPool.class.getSimpleName();
    private static final int SHUTDOWN_TIMEOUT_MILLIS = 20000;
    private static final boolean IS_DEBUGGABLE = false;

    private static SystemServerInitThreadPool sInstance;

    private ExecutorService mService = ConcurrentUtils.newFixedThreadPool(4,
            "system-server-init-thread", Process.THREAD_PRIORITY_FOREGROUND);

    public static synchronized SystemServerInitThreadPool get() {
        if (sInstance == null) {
            sInstance = new SystemServerInitThreadPool();
        }
        Preconditions.checkState(sInstance.mService != null, "Cannot get " + TAG
                + " - it has been shut down");
        return sInstance;
    }

    public Future<?> submit(Runnable runnable, String description) {
        if (IS_DEBUGGABLE) {
            return mService.submit(() -> {
                try {
                    runnable.run();
                } catch (RuntimeException e) {
                    throw e;
                }
            });
        }
        return mService.submit(runnable);
    }

    static synchronized void shutdown() {
        if (sInstance != null && sInstance.mService != null) {
            sInstance.mService.shutdown();
            boolean terminated;
            try {
                terminated = sInstance.mService.awaitTermination(SHUTDOWN_TIMEOUT_MILLIS,
                        TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(TAG + " init interrupted");
            }
            List<Runnable> unstartedRunnables = sInstance.mService.shutdownNow();
            if (!terminated) {
                throw new IllegalStateException("Cannot shutdown. Unstarted tasks "
                        + unstartedRunnables);
            }
            sInstance.mService = null; // Make mService eligible for GC
        }
    }

}