package com.dysjsjy.线程池线程回收问题;

import java.lang.ref.WeakReference;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        Executor executor = new ThreadPoolExecutor(2,
                4,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(6),
                new MyThreadFactory()
        );
        executor.execute(() -> {
            System.out.println("hello");
        });
        WeakReference<Executor> weakReference = new WeakReference<>(executor);
        executor = null;
        System.gc();
        if (weakReference.get() == null) {
            System.out.println("线程池被回收了");
        }
    }

    static class MyThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Runnable myRunable = new Runnable() {
                @Override
                public void run() {
                    System.out.println("线程开始执行了");
//                    r.run();
                    System.out.println("线程结束了");
                }
            };
            Thread thread = new Thread(myRunable, "这是我创建的线程");
            System.out.println("线程创建了");
            return thread;
        }
    }
}
