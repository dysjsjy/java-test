package com.dysjsjy.线程池;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {
    private final int coreSize;
    private final int maxSize;
    public final BlockingQueue<Runnable> blockingQueue;
    private final long timeout;
    private final TimeUnit unit;
    private final RejectHandle rejectHandle;

    public MyThreadPool(int coreSize, int maxSize, BlockingQueue<Runnable> taskQueue, long timeout, TimeUnit unit, RejectHandle rejectHandle) {
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.blockingQueue = taskQueue;
        this.timeout = timeout;
        this.unit = unit;
        this.rejectHandle = rejectHandle;
    }

    List<Thread> coreThreads = new ArrayList<>();
    List<Thread> supportThreads = new ArrayList<>();

    /**
     * @param task
     * @throws InterruptedException
     */
    public void execute(Runnable task) {
        // 如果核心线程池没满，就直接在核心线程池中创建线程
        if (coreThreads.size() < coreSize) {
            Thread thread = new CoreThread();
            coreThreads.add(thread);
            thread.start();
            return;
        }

        // 如果核心线程池满了，就将任务加入阻塞队列
        boolean offer = blockingQueue.offer(task);
        if (offer) {
            return;
        }

        /*
            先放入阻塞队列再创建核心线程会怎么样？
            线程创建时机：原实现会立即创建核心线程，而修改后的实现只有在有任务时才会创建线程。这可以节省一些系统资源，因为不会提前创建可能暂时用不到的线程。

            任务执行延迟：修改后的实现可能会导致任务在队列中等待更长时间，因为需要先放入任务，再创建线程来执行。

            资源利用率：修改后的实现更"懒加载"，可能会更节省资源，但可能会影响任务的即时执行。

            代码逻辑：修改后的实现需要更复杂的逻辑来处理线程创建和任务执行的顺序。

            public void execute(Runnable task) throws InterruptedException {
                // 先尝试放入任务
                boolean offer = blockingQueue.offer(task);
                if (offer) {
                    // 如果放入成功，再检查是否需要创建核心线程
                    if (coreThreads.size() < coreSize) {
                        Thread thread = new CoreThread();
                        coreThreads.add(thread);
                        thread.start();
                    }
                    return;
                }
                // ... 其他代码 ...
            }
         */

        // 如果阻塞队列满了，就创建支持线程
        if (coreThreads.size() + supportThreads.size() < maxSize) {
            Thread thread = new SupportThread();
            supportThreads.add(thread);
            thread.start();
            return;
        }

        // 如果支持线程池满了，就拒绝任务
        boolean offer2 = blockingQueue.offer(task);

        if (!offer2) {
            rejectHandle.reject(task, this);
        }
    }

    class CoreThread extends Thread {
        public void run() {
            while (true) {
                try {
                    Runnable task = blockingQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("CoreThread is exiting.");
                }
            }
        }
    }

    class SupportThread extends Thread {
        public void run() {
            while (true) {
                try {
                    Runnable task = blockingQueue.poll(timeout, unit);
                    if (task == null) {
                        break; 
                    }
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("SupportThread is exiting.");
                }
            }
        }
    }
}

/*
    线程池的核心是通过一个阻塞队列来实现的。
    阻塞队列是一个先进先出的队列，当队列为空时，从队列中获取元素的操作将会被阻塞，直到队列中有元素可供获取。
    当队列满时，往队列中添加元素的操作将会被阻塞，直到队列中有空闲位置可供添加。
*/