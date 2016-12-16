package org.kafka.experiment.consumer.message;

import org.apache.kafka.clients.consumer.Consumer;

/**
 * Kafka consumer factory
 */
public interface ConsumerFactory {

    Consumer<String, String> create();

    /**
     * @param groupId
     * @return New consumer with explicitly defined group
     */
    Consumer<String, String> create(String groupId);
}
