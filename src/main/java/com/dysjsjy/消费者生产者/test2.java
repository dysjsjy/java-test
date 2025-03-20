package com.dysjsjy.消费者生产者;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// 关键词：BlockingQueue, AtomicInteger, ExcutorService,

public class test2 {

    // 用于存储分割后的字符串
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    // 用于计数已处理的字符数
    private static final AtomicInteger processedChars = new AtomicInteger(0);
    // 用于生产者共享的全局索引
    private static final AtomicInteger globalIndex = new AtomicInteger(0);
    // 输入的超长字符串
    private static final String LONG_STRING = "这是一个很长的字符串用来测试多生产者和多消费者的情况我们需要将它分割并处理这是一个很长的字符串用来测试多生产者和多消费者的情况我们需要将它分割并处理";

    static class Producer implements Runnable {
        private final String name;
        private final String input;

        public Producer(String name, String input) {
            this.name = name;
            this.input = input;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int start = globalIndex.getAndAdd(3); // 每次拿3个字符
                    if (start >= input.length()) break; // 超出字符串长度则退出
                    int charsToTake = Math.min(3, input.length() - start);
                    String chunk = input.substring(start, start + charsToTake);
                    queue.put(chunk + " - Processed by Producer: " + name);
                    System.out.println("Producer " + name + " produced: " + chunk);
                    Thread.sleep(1000); // 每秒处理一次
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Consumer implements Runnable {
        private final String name;

        public Consumer(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            try {
                while (processedChars.get() < LONG_STRING.length()) {
                    String item = queue.take();
                    int charCount = item.split(" - ")[0].length(); // 获取实际字符数
                    String result = item + " - Handled by Consumer: " + name;
                    System.out.println("Consumer " + name + " consumed: " + result);
                    processedChars.addAndGet(charCount);
                    Thread.sleep(1000); // 每秒处理一次
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // 启动多个生产者，共享整个字符串
        executor.submit(new Producer("P1", LONG_STRING));
        executor.submit(new Producer("P2", LONG_STRING));

        // 启动多个消费者
        executor.submit(new Consumer("C1"));
        executor.submit(new Consumer("C2"));
        executor.submit(new Consumer("C3"));

        // 监控处理进度
        while (processedChars.get() < LONG_STRING.length()) {
            System.out.println("Processed " + processedChars.get() + " out of " + LONG_STRING.length() + " characters");
            Thread.sleep(1000);
        }

        // 关闭线程池
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("All characters processed!");
    }

}
