package com.dysjsjy.Threads.连接池;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {
    private final Semaphore semaphore;
    private final AtomicInteger atomicInteger;
    private final ScheduledExecutorService executor;
    private final int maxConnection;

    public ConnectionPool(int maxConnection) {
        this.semaphore = new Semaphore(maxConnection);
        this.maxConnection = maxConnection;
        this.atomicInteger = new AtomicInteger();
        this.executor = Executors.newScheduledThreadPool(1);
        initExcutor();
    }

    private void initExcutor() {
        executor.scheduleAtFixedRate(() -> {
            System.out.println("每秒连接数：" + atomicInteger.get());
            atomicInteger.set(0);
        }, 1, 1, TimeUnit.SECONDS);
    }

    public boolean get() {
        if (atomicInteger.incrementAndGet() > maxConnection) {
            atomicInteger.decrementAndGet();
            return false;
        }

        try {
            semaphore.acquire();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            atomicInteger.decrementAndGet();
            return false;
        }
    }

    public void release() {
        semaphore.release();
    }
}
