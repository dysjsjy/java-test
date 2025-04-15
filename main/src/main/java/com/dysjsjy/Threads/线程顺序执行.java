package com.dysjsjy.Threads;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class 线程顺序执行 {
    public static void main(String[] args) throws InterruptedException {
        f5();
    }

    static void f1() {
        AtomicInteger state = new AtomicInteger(0);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (state.compareAndSet(4, 5)) {
                        System.out.println("线程5启动成功");
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.start();
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            new Thread() {
                @Override
                public void run() {
                    int i1 = state.incrementAndGet();
                    System.out.println(idx + ": " + i1);
                }
            }.start();
        }
    }

    static void f2() {
        // CyclicBarrier 其实不太行，它是一种当执行了多少个任务后再执行之后的任务，同时启动的任务可以互相竞争，
        CyclicBarrier barrier = new CyclicBarrier(2); // 每次等待两个线程
        int THREAD_COUNT = 4;
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    System.out.println("Thread " + threadId + " is waiting");
                    barrier.await(); // 等待其他线程
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 按顺序启动线程
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i].start();
            if (i > 0) {
                try {
                    Thread.sleep(100); // 确保前一个线程先启动
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static void f3() {
        final int THREAD_COUNT = 4;
        Semaphore[] semaphores;
        semaphores = new Semaphore[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            semaphores[i] = new Semaphore(i == 0 ? 1 : 0); // 第一个线程可以立即运行
        }

        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            final int nextThreadId = (threadId + 1) % THREAD_COUNT;
            threads[i] = new Thread(() -> {
                try {
                    semaphores[threadId].acquire();
                    System.out.println("Thread " + threadId + " is running");
                    semaphores[nextThreadId].release(); // 释放下一个线程
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }
    }

    static void f4() {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            final int i1 = i;
            Thread thread = new Thread(() -> {
                System.out.println(i1);
                countDownLatch.countDown();
            });
            threadList.add(thread);
        }
        threadList.add(new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("3");
        }));
        for (Thread thread : threadList) {
            thread.start();
        }
    }

    static void f5() {
        Thread previousThread = null;

        for (int i = 0; i < 4; i++) {
            final int threadId = i;
            Thread currentThread = new Thread(() -> {
                System.out.println("Thread " + threadId + " is running");
            });

            if (previousThread != null) {
                try {
                    previousThread.join(); // 等待前一个线程完成
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            currentThread.start();
            previousThread = currentThread;
        }
    }
}
