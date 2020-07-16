package com.cups.splashin.peer2party.networker.execution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

abstract class ExecutorRunnable implements Runnable {
    private static ExecutorService executor = null;

    ExecutorRunnable(){
        if (executor == null)
            executor = new ThreadPoolExecutor(50, 200, 2, TimeUnit.MINUTES, new SynchronousQueue<>());
    }

    void submitThread(Runnable r){
        executor.execute(r);
    }
}
