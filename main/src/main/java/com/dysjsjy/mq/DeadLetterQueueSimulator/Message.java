package com.dysjsjy.mq.DeadLetterQueueSimulator;

// 消息类，包含消息内容、创建时间、重试次数和死信原因
class Message {
    private final String id;
    private final String content;
    private final long createdTime;
    private int retryCount;
    private String deadLetterReason;

    public Message(String id, String content) {
        this.id = id;
        this.content = content;
        this.createdTime = System.currentTimeMillis();
        this.retryCount = 0;
        this.deadLetterReason = null;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public String getDeadLetterReason() {
        return deadLetterReason;
    }

    public void setDeadLetterReason(String deadLetterReason) {
        this.deadLetterReason = deadLetterReason;
    }

    @Override
    public String toString() {
        return "Message{id='" + id + "', content='" + content + "', retryCount=" + retryCount +
                ", deadLetterReason='" + deadLetterReason + "'}";
    }
}
