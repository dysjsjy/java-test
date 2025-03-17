# AQS

AbstractQueuedSynchronizer（AQS）是 Java 并发包（java.util.concurrent）中的一个核心类，位于 java.util.concurrent.locks 包下。它是一个抽象类，提供了基于 FIFO（先进先出）队列的同步器框架，用于实现锁和其他同步工具（如 ReentrantLock、Semaphore、CountDownLatch 等）的底层机制。AQS 的设计思想是将同步状态（state）管理和线程的排队与阻塞抽象出来，开发者可以通过继承 AQS 并实现其模板方法来构建自定义的同步组件。

---

### 一、AQS 的核心思想
AQS 的核心是维护一个 **volatile int state**（同步状态）和一个 **双向链表队列**（CLH 队列的变种），用于管理竞争资源的线程。state 表示资源的可用性（如锁是否被占用），而队列则用于存储等待获取资源的线程。AQS 提供了两种模式：
1. **独占模式（Exclusive）**：如 ReentrantLock，只有一个线程可以持有锁。
2. **共享模式（Shared）**：如 Semaphore 或 CountDownLatch，允许多个线程同时访问资源。

开发者通过继承 AQS 并实现其抽象方法（如 tryAcquire、tryRelease 等）来定义具体的同步逻辑，而 AQS 内部负责线程的排队、阻塞和唤醒。

---

### 二、源码中的关键字段
以下是 AQS 在 Java 8 中的核心字段（摘自源码）：

```java
// 同步状态，volatile 保证可见性
private volatile int state;

// 等待队列的头节点，延迟初始化
private transient volatile Node head;

// 等待队列的尾节点，延迟初始化
private transient volatile Node tail;

// 当前持有锁的线程（仅在独占模式下使用）
private transient Thread exclusiveOwnerThread;
```

#### 1. state
- 类型：`volatile int`
- 作用：表示同步器的状态。例如，在 ReentrantLock 中，state = 0 表示锁未被占用，state > 0 表示锁被占用且可能是重入锁（state 值表示重入次数）。
- 修改方式：通过 CAS（Compare-And-Swap）操作（如 `compareAndSetState`）确保线程安全。

#### 2. Node
Node 是 AQS 内部的双向链表节点类，用于表示等待队列中的线程。它的关键字段包括：

```java
static final class Node {
    // 共享模式标识
    static final Node SHARED = new Node();
    // 独占模式标识
    static final Node EXCLUSIVE = null;

    // 节点的等待状态
    volatile int waitStatus; // 0（默认）、-1（SIGNAL）、-2（CONDITION）、-3（PROPAGATE）、1（CANCELLED）

    // 前驱节点
    volatile Node prev;

    // 后继节点
    volatile Node next;

    // 该节点代表的线程
    volatile Thread thread;

    // 下一个等待者（用于条件队列）
    Node nextWaiter;
}
```

- `waitStatus`：表示节点的状态，例如：
    - `SIGNAL (-1)`：后继节点需要被唤醒。
    - `CANCELLED (1)`：节点因超时或中断被取消。
    - `0`：默认状态。

#### 3. head 和 tail
- `head` 和 `tail` 是队列的头尾指针，使用 volatile 保证多线程下的可见性。
- 队列是一个 CLH 变种的双向链表，线程通过入队操作加入等待队列。

#### 4. exclusiveOwnerThread
- 在独占模式下，记录当前持有锁的线程。

---

### 三、核心方法
AQS 提供了一些模板方法和底层支持方法，开发者通过重写模板方法来实现自定义逻辑。以下是几个关键方法的源码分析。

#### 1. 获取资源：acquire(int arg)
`acquire` 是独占模式下获取资源的主要方法：

```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) && // 尝试获取锁，失败则进入队列
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) // 加入队列并等待
        selfInterrupt(); // 如果被中断过，则自我中断
}
```

- `tryAcquire(int)`：由子类实现，例如 ReentrantLock 中检查 state 是否为 0 并尝试用 CAS 更新。
- `addWaiter(Node.EXCLUSIVE)`：创建独占模式的 Node 节点并加入队列。
- `acquireQueued`：线程在队列中等待并尝试获取锁。

#### 2. 入队操作：addWaiter
将当前线程封装成 Node 并加入队列：

```java
private Node addWaiter(Node mode) {
    Node node = new Node(Thread.currentThread(), mode);
    Node pred = tail;
    if (pred != null) { // 队列已初始化
        node.prev = pred;
        if (compareAndSetTail(pred, node)) { // CAS 更新 tail
            pred.next = node;
            return node;
        }
    }
    enq(node); // 队列未初始化或 CAS 失败，调用 enq 自旋入队
    return node;
}
```

- 使用 CAS 确保线程安全入队。
- 如果队列未初始化（tail == null），调用 `enq` 方法自旋入队。

#### 3. 自旋入队：enq
```java
private Node enq(final Node node) {
    for (;;) { // 自旋
        Node t = tail;
        if (t == null) { // 队列为空，初始化
            if (compareAndSetHead(new Node()))
                tail = head;
        } else {
            node.prev = t;
            if (compareAndSetTail(t, node)) {
                t.next = node;
                return t;
            }
        }
    }
}
```

- 通过无限循环和 CAS 操作，确保线程安全地将节点加入队列。

#### 4. 等待获取锁：acquireQueued
```java
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor(); // 获取前驱节点
            if (p == head && tryAcquire(arg)) { // 前驱是头节点且获取锁成功
                setHead(node); // 设置当前节点为头节点
                p.next = null; // 帮助 GC
                failed = false;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) && // 是否需要阻塞
                parkAndCheckInterrupt()) // 阻塞当前线程
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node); // 失败则取消节点
    }
}
```

- 如果前驱是 head 且 `tryAcquire` 成功，则当前线程获取锁并成为头节点。
- 否则，检查是否需要阻塞（`shouldParkAfterFailedAcquire`），并通过 `LockSupport.park` 挂起线程。

#### 5. 释放资源：release(int arg)
```java
public final boolean release(int arg) {
    if (tryRelease(arg)) { // 由子类实现释放逻辑
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h); // 唤醒后继节点
        return true;
    }
    return false;
}
```

- `tryRelease(int)`：由子类实现，例如 ReentrantLock 中减少 state 值。
- `unparkSuccessor`：唤醒队列中下一个等待线程。

---

### 四、工作机制
1. **获取锁（独占模式）**：
    - 调用 `tryAcquire` 尝试获取资源。
    - 如果失败，线程封装成 Node 加入等待队列。
    - 在队列中自旋检查前驱节点是否为 head 并尝试获取锁。
    - 如果无法获取，线程被挂起（park）。

2. **释放锁**：
    - 调用 `tryRelease` 释放资源。
    - 如果 state 变为可用状态，唤醒队列中的下一个线程（unpark）。

3. **共享模式**：
    - 使用 `acquireShared` 和 `releaseShared`，逻辑类似，但允许多个线程同时持有资源。

---

### 五、ReentrantLock 示例
以 ReentrantLock 的实现为例：
- `tryAcquire`：检查 state 是否为 0，若是则用 CAS 设置为 1；若当前线程已持有锁，则 state 自增（支持重入）。
- `tryRelease`：减少 state 值，若降为 0 则释放锁。

```java
// ReentrantLock 的 NonfairSync 实现
protected final boolean tryAcquire(int acquires) {
    Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) { // 锁未被占用
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    } else if (current == getExclusiveOwnerThread()) { // 重入
        int nextc = c + acquires;
        setState(nextc);
        return true;
    }
    return false;
}
```

---

### 六、总结
AQS 是 Java 并发编程的基石，通过 volatile state 和 CLH 队列实现了高效的线程同步。它将锁的获取与释放抽象为模板方法，由子类实现具体逻辑，而底层通过 CAS 和 LockSupport 提供了线程安全的队列管理和阻塞/唤醒机制。理解 AQS 的源码需要关注 state 的管理、Node 队列的构建以及 CAS 操作的线程安全设计。

如果你有具体问题或需要更深入的某部分分析，欢迎继续提问！