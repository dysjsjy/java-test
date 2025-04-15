package com.dysjsjy.juc.AQS;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MyLock myLock = new MyLock();
        List<Thread> threads = new ArrayList<>();
        int[] count = new int[] {1000};
        for (int i = 0; i < 30; i++) {
            threads.add(new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        myLock.lock();
                        count[0]--;
                    }

                    for (int j = 0; j < 10; j++) {
                        myLock.unlock();
                    }
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("count:" + count[0]);
    }
}
