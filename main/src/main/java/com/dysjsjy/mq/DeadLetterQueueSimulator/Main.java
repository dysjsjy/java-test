package com.dysjsjy.mq.DeadLetterQueueSimulator;

// 一个队列中失败的任务放到另一专门处理失败任务的队列中处理
public class Main {
    // 主方法：运行模拟器
    public static void main(String[] args) throws InterruptedException {
        DeadLetterQueueSimulator simulator = new DeadLetterQueueSimulator();
        simulator.start();
        // 运行10秒后停止
        Thread.sleep(10000);
        simulator.stop();
    }
}
