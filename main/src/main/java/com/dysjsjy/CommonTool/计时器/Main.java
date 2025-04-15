package com.dysjsjy.CommonTool.计时器;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss SSS");
        ScheduleService scheduleService = new ScheduleService();
        scheduleService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务1执行了，每100毫秒执行一次：" + LocalDateTime.now().format(formatter));
            }
        }, 100);

        scheduleService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务2执行了，每200毫秒执行一次：" + LocalDateTime.now().format(formatter));
            }
        }, 200);
    }
}
