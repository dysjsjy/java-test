package com.dysjsjy.AQS;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;


public class MyLock {
    private AtomicInteger state = new AtomicInteger(0);
    private Thread owner;
    private AtomicReference<Node> head = new AtomicReference<>(new Node());
    private AtomicReference<Node> tail = new AtomicReference<>(head.get());

    // 加锁
    void lock() {
        if (state.compareAndSet(0, 1)) {
            owner = Thread.currentThread();
            return;
        } else {
            if (owner == Thread.currentThread()) {
                int i = state.incrementAndGet();
                System.out.println("获得了重入锁，次数为：" + i);
                return;
            }
        }

        // 都没有拿到锁，就加入队列
        Node current = new Node();
        current.thread = Thread.currentThread();
        // 问题：为什么这里要用while循环？不能直接用if？
        while (true) {
            Node currentTail = tail.get();
            if (tail.compareAndSet(currentTail, current)) {
                System.out.println(Thread.currentThread() + "加入链表队尾。");
                current.pre = currentTail;
                currentTail.next = current;
                break;
            }
        }

        // 自旋
        // 问题：为什么这里要用while循环？不能直接用if？
        while (true) {
            if (current.pre == head.get() && state.compareAndSet(0, 1)) {
                owner = Thread.currentThread();
                head.set(current);
                current.pre.next = null;
                current.pre = null;
                System.out.println(Thread.currentThread() + "被唤醒之后拿到了锁。");
                return;
            }

            LockSupport.park();
        }
    }

    // 解锁
    void unlock() {
        if (owner != Thread.currentThread()) {
            throw new RuntimeException("当前线程不是锁的持有者");
        }

        int i = state.get();

        if (i > 1) {
            state.decrementAndGet();
            System.out.println(Thread.currentThread() + "释放了重入锁，次数为：" + state.get());
            return;
        }

        if (i <= 0) {
            throw new RuntimeException("锁的状态异常");
        }

//        // 问题：为什么这里又不需要使用while了？
//        state.set(0);
//        Node next = head.get().next;
//        if (next == null) {
//            // 这里的owner其实也不是必要的因为owner在lock中进行了更替
////            owner = null;
//            return;
//        }
//        // 注意这里不能对head进行任何操作，head的控制在lock中执行了，再执行会出错
//        LockSupport.unpark(next.thread);

        Node headNode = head.get();
        Node next = headNode.next;

        state.set(0);

        if (next == null) {
            owner = null;
            return;
        }

        System.out.println(Thread.currentThread().getName() + "唤醒了" + next.thread.getName());
        LockSupport.unpark(next.thread);
    }


    static class Node {
        Node pre;
        Node next;
        Thread thread;
    }
}
