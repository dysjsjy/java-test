package com.dysjsjy.线程池;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        MyThreadPool myThreadPool = new MyThreadPool(2, 4, new ArrayBlockingQueue<>(2), 1, TimeUnit.SECONDS, new DiscardRejectHandle());
        for (int i = 0; i < 8; i++) {
            final int fi = i;
            myThreadPool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + " " + fi);
            });
        }
        System.out.println("主线程没有被阻塞");

    }
}