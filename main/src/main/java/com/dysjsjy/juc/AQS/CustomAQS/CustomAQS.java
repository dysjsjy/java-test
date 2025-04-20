package com.dysjsjy.juc.AQS.CustomAQS;
/*
    有bug
 */

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;


public abstract class CustomAQS {
    private final AtomicInteger state = new AtomicInteger(0);
    private volatile Thread exclusiveOwnerThread;
    private final AtomicReference<Node> tail = new AtomicReference<>();
    private volatile Node head;

    static final class Node {
        volatile Thread thread;
        volatile Node prev;
        volatile Node next;
        volatile boolean canceled;

        Node() {} // Sentinel node

        Node(Thread thread) {
            this.thread = thread;
        }
    }

    protected abstract boolean tryAcquire(int acquires);
    protected abstract boolean tryRelease(int releases);

    protected final int getState() {
        return state.get();
    }

    protected final void setState(int newState) {
        state.set(newState);
    }

    protected final boolean compareAndSetState(int expect, int update) {
        return state.compareAndSet(expect, update);
    }

    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }

    protected final boolean hasQueuedPredecessors() {
        Node h = head;
        Node t = tail.get();
        if (h == null || t == null) {
            return false;
        }
        Node first = h.next;
        return first != null && first.thread != Thread.currentThread();
    }

    public void acquire(int arg) {
        if (!tryAcquire(arg)) {
            Node node = addWaiter();
            acquireQueued(node, arg);
        }
    }

    private Node addWaiter() {
        Node node = new Node(Thread.currentThread());
        for (;;) {
            Node pred = tail.get();
            if (pred == null) {
                initializeQueue();
                pred = tail.get();
            }
            node.prev = pred;
            if (tail.compareAndSet(pred, node)) {
                pred.next = node;
                return node;
            }
        }
    }

    private void initializeQueue() {
        Node sentinel = new Node();
        if (tail.compareAndSet(null, sentinel)) {
            head = sentinel;
        }
    }

    private void acquireQueued(Node node, int arg) {
        boolean interrupted = false;
        try {
            for (;;) {
                Node pred = node.prev;
                if (pred == head && tryAcquire(arg)) {
                    setHead(node);
                    pred.next = null; // Help GC
                    return;
                }
                LockSupport.park(this);
                if (Thread.interrupted()) {
                    interrupted = true;
                    cancelAcquire(node);
                    break;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void cancelAcquire(Node node) {
        if (node == null) return;

        node.thread = null;
        node.canceled = true;

        // Skip canceled predecessors
        Node pred = node.prev;
        while (pred != null && pred.canceled) {
            node.prev = pred.prev;
            pred = pred.prev;
        }

        Node predNext = pred != null ? pred.next : null;
        if (pred != null) {
            pred.next = node.next;
        }

        Node next = node.next;
        if (next != null) {
            next.prev = pred;
        }

        if (node == tail.get()) {
            tail.compareAndSet(node, pred);
        }
    }

    private void setHead(Node node) {
        head = node;
        node.prev = null;
        node.thread = null;
    }

    public boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            unparkSuccessor(h);
            return true;
        }
        return false;
    }

    private void unparkSuccessor(Node node) {
        Node s = node.next;
        if (s == null || s.canceled) {
            s = null;
            for (Node t = tail.get(); t != null && t != node; t = t.prev) {
                if (!t.canceled) {
                    s = t;
                }
            }
        }
        if (s != null) {
            LockSupport.unpark(s.thread);
        }
    }

    static class NonfairSync extends CustomAQS {
        @Override
        protected boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {
                setState(c + acquires);
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException();
            }
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
    }

    static class FairSync extends CustomAQS {
        @Override
        protected boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {
                setState(c + acquires);
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException();
            }
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
    }
}