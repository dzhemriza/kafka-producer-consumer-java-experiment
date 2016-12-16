package org.kafka.experiment.producer.message.impl;

/**
 * Kafka producer configuration
 */
public class ProducerConfig {

    /**
     * Kafka Topic
     */
    private final String topic;

    /**
     * Kafka Topic Partition
     */
    private final int partition;

    /**
     * Number of random messages that producer have to produce.
     */
    private final int numberOfMessages;

    public ProducerConfig(String topic, int partition, int numberOfMessages) {
        this.topic = topic;
        this.partition = partition;
        this.numberOfMessages = numberOfMessages;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }
}
