package com.dysjsjy.Threads.连接池;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/*
    多线程竞争一个连接池，连接池每秒最多能处理100条连接，前100条返回true，后100条返回false；
 */
public class Main {
    public static void main(String[] args) {
        ConnectionPool pool = new ConnectionPool(10);
        Executor executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 2; i++) {
            executor.execute(new Thread(() -> {
                while (true) {
                    boolean b = pool.get();
                    System.out.println(Thread.currentThread().getName() + ": " + b);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    pool.release();
                }
            }));
        }
    }
}
