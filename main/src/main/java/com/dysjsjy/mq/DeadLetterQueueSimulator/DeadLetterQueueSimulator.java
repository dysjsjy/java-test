package com.dysjsjy.mq.DeadLetterQueueSimulator;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// 死信队列模拟器
public class DeadLetterQueueSimulator {
    private final BlockingQueue<Message> normalQueue;
    private final BlockingQueue<Message> deadLetterQueue;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService executor;
    private final AtomicInteger messageIdGenerator;
    private static final long TTL = 5000; // 消息TTL为5秒
    private static final int MAX_RETRIES = 3; // 最大重试次数
    private static final int QUEUE_CAPACITY = 10; // 普通队列容量

    public DeadLetterQueueSimulator() {
        this.normalQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.deadLetterQueue = new LinkedBlockingQueue<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.executor = Executors.newFixedThreadPool(3);
        this.messageIdGenerator = new AtomicInteger(0);
    }

    // 启动模拟器
    public void start() {
        // 启动生产者
        executor.submit(this::produceMessages);
        // 启动普通队列消费者
        executor.submit(this::consumeNormalQueue);
        // 启动死信队列消费者
        executor.submit(this::consumeDeadLetterQueue);
        // 启动TTL检查任务
        scheduler.scheduleAtFixedRate(this::checkExpiredMessages, 1, 1, TimeUnit.SECONDS);
    }

    // 停止模拟器
    public void stop() {
        scheduler.shutdown();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    // 生产者：向普通队列发送消息
    private void produceMessages() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String messageId = "MSG-" + messageIdGenerator.incrementAndGet();
                Message message = new Message(messageId, "Content-" + messageId);
                if (!normalQueue.offer(message)) {
                    // 队列满，放入死信队列
                    message.setDeadLetterReason("Queue full");
                    deadLetterQueue.offer(message);
                    System.out.println("Queue full, moved to DLQ: " + message);
                } else {
                    System.out.println("Produced: " + message);
                }
                Thread.sleep(1000); // 模拟生产间隔
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 消费者：处理普通队列的消息
    private void consumeNormalQueue() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = normalQueue.take();
                try {
                    // 模拟消费失败（例如ID包含"3"时失败）
                    if (message.getId().contains("3")) {
                        throw new RuntimeException("Simulated consumption failure");
                    }
                    System.out.println("Consumed: " + message);
                } catch (Exception e) {
                    message.incrementRetryCount();
                    if (message.getRetryCount() >= MAX_RETRIES) {
                        message.setDeadLetterReason("Max retries exceeded: " + e.getMessage());
                        deadLetterQueue.offer(message);
                        System.out.println("Moved to DLQ: " + message);
                    } else {
                        normalQueue.offer(message); // 重试
                        System.out.println("Retry: " + message);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 死信队列消费者：处理死信消息
    private void consumeDeadLetterQueue() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = deadLetterQueue.take();
                System.out.println("Processed DLQ message: " + message);
                // TODO: 根据业务需求处理死信消息，例如记录日志或存入数据库
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 检查过期消息
    private void checkExpiredMessages() {
        long currentTime = System.currentTimeMillis();
        normalQueue.removeIf(message -> {
            if (currentTime - message.getCreatedTime() > TTL) {
                message.setDeadLetterReason("Message expired");
                deadLetterQueue.offer(message);
                System.out.println("Expired, moved to DLQ: " + message);
                return true;
            }
            return false;
        });
    }
}
