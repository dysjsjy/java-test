package com.dysjsjy.CommonTool.SimpleReentrantLock;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        SimpleReentrantLock lock = new SimpleReentrantLock();

        // 创建多个线程来测试锁
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();

            System.out.println(threadName + " 尝试获取锁");

            if (lock.tryLock()) {
                try {
                    System.out.println(threadName + " 成功获取锁，正在工作");
                    Thread.sleep(1000); // 模拟工作

                    // 测试重入
                    lock.lock(); // 再次获取锁（重入）
                    try {
                        System.out.println(threadName + " 成功重入锁");
                        Thread.sleep(500); // 模拟更多工作
                    } finally {
                        lock.unlock(); // 释放重入锁
                        System.out.println(threadName + " 释放了重入锁");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock(); // 释放最初的锁
                    System.out.println(threadName + " 释放了锁");
                }
            } else {
                System.out.println(threadName + " 未能立即获取锁");
            }
        };

        // 创建并启动三个线程
        Thread thread1 = new Thread(task, "Thread-1");
        Thread thread2 = new Thread(task, "Thread-2");
        Thread thread3 = new Thread(task, "Thread-3");

        thread1.start();
        thread2.start();
        thread3.start();

        // 主线程等待一会，检查锁状态
        Thread.sleep(500);

        // 测试锁的状态
        System.out.println("当前是否有线程等待锁: " + lock.hasQueuedThreads());
        System.out.println("锁当前是否被持有: " + lock.isLocked());

        // 等待所有线程完成
        thread1.join();
        thread2.join();
        thread3.join();

        // 最终检查
        System.out.println("所有线程完成，当前是否有线程等待锁: " + lock.hasQueuedThreads());
        System.out.println("锁当前是否被持有: " + lock.isLocked());
    }
}
