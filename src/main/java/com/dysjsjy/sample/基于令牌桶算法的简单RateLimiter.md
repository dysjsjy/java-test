
---

### 令牌桶算法原理

令牌桶算法的工作机制如下：

1. **令牌桶**：有一个固定容量的桶，用于存放令牌。
2. **令牌生成**：令牌以固定速率（如每秒若干个）被放入桶中。
3. **请求处理**：每次请求到来时，需要从桶中取出一个令牌才能被处理。
4. **无令牌时的行为**：如果桶中没有令牌，请求要么被拒绝，要么等待令牌生成。

基于这个原理，我们需要实现一个线程安全的 RateLimiter，能够动态生成令牌并控制请求速率。

---

### 实现思路

为了实现 RateLimiter，我们需要：

1. **核心变量**：
   - `capacity`：桶的最大容量。
   - `rate`：令牌生成速率（每秒生成多少令牌）。
   - `tokens`：当前桶中的令牌数量。
   - `lastRefillTime`：上一次生成令牌的时间。

2. **线程安全**：
   - 使用 `ReentrantLock` 保护共享变量的访问。
   - 使用 `Condition` 让线程在没有令牌时等待。

3. **功能方法**：
   - `refill()`：根据时间间隔生成令牌。
   - `tryAcquire()`：尝试获取令牌（非阻塞）。
   - `acquire()`：获取令牌（阻塞，直到有令牌）。

4. **动态生成令牌**：
   - 不使用定时器定期生成令牌，而是在每次请求时根据时间间隔动态计算生成的令牌数量，这样更高效。

---
<!-- com.dysjsjy -->
### 代码实现

以下是基于 JUC 的 `TokenBucketRateLimiter` 类的完整实现：

```java
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
```

<!-- com.dysjsjy -->

---

### 实现说明

1. **成员变量**：
   - `capacity` 和 `rate` 在构造时指定，分别表示桶容量和令牌生成速率。
   - `tokens` 使用 `double` 类型以提高精度，初始值为桶容量。
   - `lastRefillTime` 使用 `System.nanoTime()` 获取高精度时间。

2. **refill 方法**：
   - 计算当前时间与上一次生成时间的时间差。
   - 根据时间差和速率计算新生成的令牌数。
   - 更新 `tokens`，但不超过 `capacity`。
   - 更新 `lastRefillTime`。

3. **tryAcquire 方法**：
   - 加锁后调用 `refill` 生成令牌。
   - 如果 `tokens` 足够（>=1），消耗一个令牌并返回 `true`。
   - 否则返回 `false`。

4. **acquire 方法**：
   - 加锁后调用 `refill` 生成令牌。
   - 如果有足够令牌，消耗一个并返回。
   - 否则计算下一次令牌生成所需的时间，使用 `condition.awaitNanos` 等待。
   - 等待后循环检查，直到获取成功。

5. **线程安全**：
   - 使用 `ReentrantLock` 确保对 `tokens` 和 `lastRefillTime` 的访问是线程安全的。
   - 使用 `Condition` 实现阻塞等待。

---
<!-- com.dysjsjy -->
### 使用示例

以下是一个简单的测试代码，展示如何使用 `TokenBucketRateLimiter`：

```java
public class Main {
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
```

**预期输出**：
- 前 10 个请求会立即处理（桶初始有 10 个令牌）。
- 之后的请求会以大约 200ms 的间隔处理（因为速率是 5 个/秒，每 200ms 生成一个令牌）。

---

### 优点与优化

1. **优点**：
   - 使用 `double` 表示令牌数量，提高了速率控制的精度。
   - 动态生成令牌，避免了定时器的开销。
   - 线程安全，支持多线程并发访问。

2. **可能的优化**：
   - 添加突发流量支持（允许短时间内超过速率）。
   - 减少浮点数计算误差，使用更精确的时间处理。
   - 添加超时机制，避免 `acquire` 无限等待。

---
<!-- com.dysjsjy -->
### 总结

通过使用 JUC 中的 `ReentrantLock` 和 `Condition`，我们实现了一个基于令牌桶算法的 RateLimiter。这个实现能够高效地限制请求速率，同时保证线程安全。开发者可以根据实际需求调整容量和速率，或者扩展功能以满足更复杂场景。