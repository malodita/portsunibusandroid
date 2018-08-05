package com.malcolm.unibusutilities.utils;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static final int THREAD_COUNT = 2;


    private static AppExecutors instance;
    private final Executor backgroundThread;
    private final Executor mainThread;

    AppExecutors(Executor backgroundThread, Executor mainThread) {
        this.backgroundThread = backgroundThread;
        this.mainThread = mainThread;
    }

    public static AppExecutors getInstance(){
        if (instance == null) {
            instance = (new AppExecutors(new BackgroundThreadExecutor(), new MainThreadExecutor()));
        }
        return instance;
    }

    public Executor getMainThreadExecutor() {
        return mainThread;
    }

    public Executor getBackgroundThread() {
        return backgroundThread;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    private static class HighPriorityThreadExecutor implements Executor {

        private final Executor mDiskIO;

        HighPriorityThreadExecutor() {
            mDiskIO = Executors.newSingleThreadExecutor();
        }

        @Override
        public void execute(@NonNull Runnable command) {
            mDiskIO.execute(command);
        }
    }

    private static class BackgroundThreadExecutor implements Executor {

        private final Executor mDiskIO;

        BackgroundThreadExecutor() {
            mDiskIO = Executors.newFixedThreadPool(THREAD_COUNT);
        }

        @Override
        public void execute(@NonNull Runnable command) {
            mDiskIO.execute(command);
        }
    }
}
