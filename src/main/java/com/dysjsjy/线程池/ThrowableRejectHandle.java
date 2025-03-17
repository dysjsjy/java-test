package com.dysjsjy.线程池;

public class ThrowableRejectHandle implements RejectHandle {

    @Override
    public void reject(Runnable task, MyThreadPool myThreadPool) {
        throw new RuntimeException("Task queue is full and timeout, reject task.");
    }
    
}
