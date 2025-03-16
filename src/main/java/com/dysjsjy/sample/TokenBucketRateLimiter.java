package com.dysjsjy.sample;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucketRateLimiter {
    // 锁，用于保护共享变量
    private final ReentrantLock lock = new ReentrantLock();
    // 条件变量，用于等待令牌
    private final Condition condition = lock.newCondition();
    // 桶的最大容量
    private final int capacity;
    // 令牌生成速率（每秒生成多少令牌）
    private final double rate;
    // 当前桶中的令牌数量
    private double tokens;
    // 上一次生成令牌的时间（纳秒）
    private long lastRefillTime;

    /**
     * 构造函数
     * @param capacity 桶的容量
     * @param rate 令牌生成速率（每秒令牌数）
     */
    public TokenBucketRateLimiter(int capacity, double rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.tokens = capacity; // 初始化时桶满
        this.lastRefillTime = System.nanoTime();
    }

    /**
     * 生成令牌
     */
    private void refill() {
        long now = System.nanoTime();
        // 计算时间间隔（秒）
        double timeElapsed = (now - lastRefillTime) / 1e9;
        // 根据速率计算新生成的令牌数
        double newTokens = timeElapsed * rate;
        // 更新令牌数量，但不超过容量
        tokens = Math.min(capacity, tokens + newTokens);
        // 更新上一次生成时间
        lastRefillTime = now;
    }

    /**
     * 尝试获取令牌（非阻塞）
     * @return 如果获取成功返回 true，否则返回 false
     */
    public boolean tryAcquire() {
        lock.lock();
        try {
            refill(); // 先尝试生成令牌
            if (tokens >= 1) {
                tokens -= 1; // 消耗一个令牌
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取令牌（阻塞，直到有令牌可用）
     * @throws InterruptedException 如果线程被中断
     */
    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                refill(); // 先尝试生成令牌
                if (tokens >= 1) {
                    tokens -= 1; // 消耗一个令牌
                    return;
                }
                // 计算需要等待的时间（秒）
                double waitTime = (1 - tokens) / rate;
                // 转换为纳秒并等待
                condition.awaitNanos((long) (waitTime * 1e9));
            }
        } finally {
            lock.unlock();
        }
    }
}