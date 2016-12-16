package org.kafka.experiment.producer.message.impl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * High level interface for {@link KafkaRandomMessageProducer} bootstrap.
 */
public interface KafkaProducerBootstrapStrategy {

    /**
     * Starts {@link KafkaRandomMessageProducer} instances based on implemented strategy.
     *
     * @param producersConfigs
     * @param pool
     * @param tasks
     */
    void bootstrap(List<ProducerConfig> producersConfigs, ExecutorService pool, List<Future<?>> tasks);
}
