package com.dysjsjy.计时器;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.LockSupport;

public class ScheduleService {

    Trigger trigger = new Trigger();
    ExecutorService executorService = Executors.newFixedThreadPool(6);

    void schedule(Runnable task, long delay) {
        Job job = new Job();
        job.setStartTime(System.currentTimeMillis() + delay);
        job.setDelay(delay);
        job.setRunnable(task);
        trigger.queue.add(job);
        trigger.wakeup();
    }

    class Trigger {

        PriorityBlockingQueue<Job> queue = new PriorityBlockingQueue<>();

        Thread thread = new Thread(() -> {
            // 管理task同时运行task
            while (true) {
                // 当队列为空时，park
                while (queue.isEmpty()) {
                    LockSupport.park();
                }
                try {
                    Job job = queue.peek();
                    if (job.getStartTime() < System.currentTimeMillis()) {
                        // 执行任务
                        queue.poll();
                        Runnable task = job.getRunnable();
                        executorService.execute(task);
                        Job nextJob = new Job();
                        nextJob.setStartTime(System.currentTimeMillis() + job.getDelay());
                        nextJob.setDelay(job.getDelay());
                        nextJob.setRunnable(job.getRunnable());
                        queue.offer(nextJob);
                    } else {
                        LockSupport.parkUntil(job.getStartTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        {
            thread.start();
            System.out.println("Trigger启动了。");
        }

        // 当新任务加入时，手动唤醒一次Trigger
        void wakeup() {
            LockSupport.unpark(thread);
        }
    }

    // 添加关闭方法
    public void shutdown() {
        executorService.shutdown();
        trigger.thread.interrupt();
    }
}
