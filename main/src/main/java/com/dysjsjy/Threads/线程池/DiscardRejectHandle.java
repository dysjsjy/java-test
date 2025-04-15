package com.dysjsjy.Threads.线程池;

public class DiscardRejectHandle implements RejectHandle {
    @Override
    public void reject(Runnable task, MyThreadPool myThreadPool) {
        myThreadPool.blockingQueue.poll();
        myThreadPool.execute(task);
    }
}