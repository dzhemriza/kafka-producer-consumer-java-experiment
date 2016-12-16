package org.kafka.experiment.utils;

import java.util.UUID;

/**
 * This class represents the message that is going to be send to kafka.
 */
public class Message {

    private UUID uuid;
    private long time;
    private long sequenceNumber;
    private String data;
    private boolean isLast;

    @Deprecated
    private Message() {
        // For json serialization only
    }

    public Message(UUID uuid, long time, long sequenceNumber, String data, boolean isLast) {
        this.uuid = uuid;
        this.time = time;
        this.sequenceNumber = sequenceNumber;
        this.data = data;
        this.isLast = isLast;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
