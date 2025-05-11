package com.dysjsjy.Threads.多线程输出字符串;

import java.util.concurrent.locks.ReentrantLock;

public class Main {

    // 一把锁无脑秒
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        int[] cnt = new int[1];
        cnt[0] = 0;

        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    lock.lock();
                    if (cnt[0] % 2 == 0) {
                        System.out.print(cnt[0] + ", ");
                        cnt[0]++;
                    }
                    if (cnt[0] == 101) {
                        lock.unlock();
                        break;
                    }
                    lock.unlock();
                }
            };
        };

        Thread thread2 = new Thread() {
            public void run() {
                while (true) {
                    lock.lock();
                    if (cnt[0] % 2 != 0) {
                        System.out.print(cnt[0] + ", ");
                        cnt[0]++;
                    }
                    if (cnt[0] == 100) {
                        lock.unlock();
                        break;
                    }
                    lock.unlock();
                }
            };
        };

        thread.start();
        thread2.start();
    }
}


