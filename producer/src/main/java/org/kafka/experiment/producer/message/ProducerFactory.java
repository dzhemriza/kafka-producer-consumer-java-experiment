package org.kafka.experiment.producer.message;

import org.apache.kafka.clients.producer.Producer;

/**
 * Factoring for creating Kafka message producers
 */
public interface ProducerFactory {

    Producer<String, String> create();
}
