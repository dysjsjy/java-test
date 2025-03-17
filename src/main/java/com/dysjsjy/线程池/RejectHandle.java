package com.dysjsjy.线程池;

public interface RejectHandle {

    void reject(Runnable task, MyThreadPool myThreadPool);
}