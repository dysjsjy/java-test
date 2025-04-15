package com.dysjsjy.Lock.CustomBlockingQueue;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// 实际上就是在从LinkedList中加入和拿出的时候加锁，然后拿不到取不到就等待，有取到拿到的就唤醒等待的，
// 关键词：ReentrantLock, capacity, Condition, notFull, notEmpty,
public class CustomBlockingQueue<T> {
    private LinkedList<T> queue;
    private int capacity;
    private ReentrantLock lock;
    private Condition notFull;
    private Condition notEmpty;

    public CustomBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    public void put(T element) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() >= capacity) { // 队列满，等待
                notFull.await();
            }
            queue.add(element);
            notEmpty.signal(); // 通知消费者队列非空
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) { // 队列空，等待
                notEmpty.await();
            }
            T item = queue.removeFirst();
            notFull.signal(); // 通知生产者队列非满
            return item;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
