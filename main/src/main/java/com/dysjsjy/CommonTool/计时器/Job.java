package com.dysjsjy.CommonTool.计时器;

public class Job implements Comparable<Job> {
    private long startTime;
    private long delay;
    private Runnable runnable;

    public Job(long startTime, long delay, Runnable runnable) {
        this.startTime = startTime;
        this.delay = delay;
        this.runnable = runnable;
    }

    public Job() {
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public int compareTo(Job o) {
        return Long.compare(this.startTime, o.startTime);
    }
}
