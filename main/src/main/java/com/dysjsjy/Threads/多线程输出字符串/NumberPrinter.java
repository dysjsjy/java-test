package com.dysjsjy.Threads.多线程输出字符串;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NumberPrinter {
    private int max;
    private int cnt;
    private ReentrantLock lock;
    private Condition evenCondition;
    private Condition oddCondition;

    public NumberPrinter(int max) {
        this.max = max;
        this.cnt = 0;
        this.lock = new ReentrantLock();
        this.evenCondition = lock.newCondition();
        this.oddCondition = lock.newCondition();
    }

    // 输出偶数
    public void printEven() {
        while (true) {
            try {
                lock.lock();
                if (cnt % 2 != 0) {
                    evenCondition.await();
                }
                if (cnt > max) {
                    oddCondition.signal();
                    return;
                }
                System.out.print(cnt);
                if (cnt != max) {
                    System.out.print(", ");
                }
                cnt++;
                oddCondition.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    // 输出奇数
    public void printOdd() {
        while (true) {
            try {
                lock.lock();
                if (cnt % 2 == 0) {
                    oddCondition.await();
                }
                if (cnt > max) {
                    evenCondition.signal();
                    return;
                }
                System.out.print(cnt);
                if (cnt != max) {
                    System.out.print(", ");
                }
                cnt++;
                evenCondition.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        NumberPrinter numberPrinter = new NumberPrinter(99);
        Thread threadEvnThread = new Thread(() -> {
            numberPrinter.printEven();
        });
        Thread threadOddThread = new Thread(() -> {
            numberPrinter.printOdd();
        });

        threadEvnThread.start();
        threadOddThread.start();
    }

}
