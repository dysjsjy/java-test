package com.dysjsjy.消费者生产者;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class test {
    // 多生产者多消费者处理字符串
    // 使用CAS AtomicInteger进行生产者线程同步
    static final BlockingQueue<Runnable> commands = new LinkedBlockingQueue<>();
    static final AtomicInteger globalIndex = new AtomicInteger(0);

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append("hello world: " + i + ".");
        }

        Production p = new Production(sb.toString(), 8);
        Production p1 = new Production(sb.toString(), 3);
        Customer c = new Customer();
        Customer c1 = new Customer();

        p.start();
        p1.start();
        c.start();
        c1.start();
    }

    static class Customer extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable command = commands.take();
                    command.run();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    System.out.println("Customer over.");
                }
            }
        }
    }

    static class Production extends Thread {
        String str;
        int length;

        public Production(String str, int length) {
            this.str = str;
            this.length = length;
        }

        @Override
        public void run() {
            while (true) {
                int start = globalIndex.getAndAdd(length);
                if (start > str.length()) {
                    break;
                }
                Runnable command = getRunnable(start);

                boolean offer = commands.offer(command);
                if (!offer) {
                    System.out.println("加入失败。");
                }

            }
        }

        private Runnable getRunnable(int start) {
            int end = Math.min(start + length, str.length());
            String finalS = str.substring(start, end);
            return new Runnable() {
                @Override
                public void run() {
                    System.out.println(finalS);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }
}
