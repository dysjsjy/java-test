package com.dysjsjy.sample.test;

import com.dysjsjy.sample.TokenBucketRateLimiter;

public class TokenBucketRateLimiterTest {
    public static void main(String[] args) throws InterruptedException {
        // 创建一个容量为 10，速率为 5 个/秒的 RateLimiter
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(10, 5.0);

        // 模拟多个请求
        for (int i = 0; i < 20; i++) {
            long start = System.currentTimeMillis();
            limiter.acquire(); // 获取令牌，可能阻塞
            long end = System.currentTimeMillis();
            System.out.println("Request " + i + " processed at " + (end - start) + "ms");
        }
    }
}
