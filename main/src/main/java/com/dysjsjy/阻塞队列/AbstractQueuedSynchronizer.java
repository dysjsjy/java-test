package com.dysjsjy.阻塞队列;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer {

    AtomicInteger state = new AtomicInteger(0);
    AtomicReference<Node> head = new AtomicReference<>(new Node());
    AtomicReference<Node> tail = new AtomicReference<>(head.get());

    // 获取锁
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    // 释放锁
    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    // 添加等待线程到队列
    final void acquire(int arg) {
        // 如果没有人用锁，直接获得并返回
        if (state.compareAndSet(0, 1)) {
            setExclusiveOwnerThread(Thread.currentThread());
            return;
        } else {
            // 如果当前的锁就在自己手里，增加state，返回
            if (getExclusiveOwnerThread() == Thread.currentThread()) {
                int andIncrement = state.incrementAndGet();
                System.out.println("当前重入锁次数为：" + andIncrement);
                return;
            }
        }

        // 锁是别人的，加入队列
        Node current = new Node();
        current.thread = Thread.currentThread();
        while (true) {
            Node currentTail = tail.get();
            if (tail.compareAndSet(currentTail, current)) {
                System.out.println(Thread.currentThread().getName() + "加入队列");
                current.prev = currentTail;
                currentTail.next = current;
                break;
            }
        }

        // 自旋等待
        while (true) {
            if (current.prev == head.get() && state.compareAndSet(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                head.set(current);
                current.prev.next = null;
                current.prev = null;
                System.out.println(Thread.currentThread().getName() + "被唤醒后拿到了锁");
            }
            LockSupport.park();
        }
    }

    // 释放资源，唤醒等待线程
    final void release(int arg) {
        if (tryRelease(arg)) {
            // 唤醒等待队列中的下一个线程
            LockSupport.unpark(getFirstQueuedThread());
        }
    }

    // 获取第一个等待线程（简化）
    private Thread getFirstQueuedThread() {
        return head.get().next.thread;
    }

    protected final int getState() {
        return state.get();
    }

    protected final void setState(int newState) {
        state.set(newState);
    }

    protected final boolean compareAndSetState(int expect, int update) {
        return state.compareAndSet(expect, update);
    }

    public final boolean hasQueuedThreads() {
        for (Node p = tail.get(), h = head.get(); p != h && p != null; p = p.prev)
            if (p.status >= 0)
                return true;
        return false;
    }

    public final boolean hasQueuedPredecessors() {
        Thread first = null; Node h, s;
        if ((h = head.get()) != null && ((s = h.next) == null ||
                (first = s.thread) == null ||
                s.prev == null))
            first = getFirstQueuedThread(); // retry via getFirstQueuedThread
        return first != null && first != Thread.currentThread();
    }

    static class Node {
        volatile Node prev;       // initially attached via casTail
        volatile Node next;       // visibly nonnull when signallable
        Thread thread;            // visibly nonnull when enqueued
        volatile int status;      // written by owner, atomic bit ops by others

        public Node() {
        }

        public Node(Thread thread) {
            this.thread = thread;
        }
    }
}
