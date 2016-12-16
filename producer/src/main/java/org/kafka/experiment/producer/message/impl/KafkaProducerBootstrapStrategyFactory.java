package org.kafka.experiment.producer.message.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Map between instances of the strategy
 */
@Component
public class KafkaProducerBootstrapStrategyFactory {

    @Autowired
    private Environment environment;

    @Autowired
    private KafkaProducerBootstrapStrategy[] strategies;

    /**
     * @return Creates {@link KafkaProducerBootstrapStrategy} based on configuration file
     */
    public KafkaProducerBootstrapStrategy createStrategy() {
        String className = environment.getProperty("app.kafka.random.message.coordinator.producer.strategy");

        for (KafkaProducerBootstrapStrategy strategy : strategies) {
            if (strategy.getClass().getCanonicalName().equals(className)) {
                return strategy;
            }
        }

        throw new RuntimeException("Invalid class name 'app.kafka.random.message.coordinator.producer.strategy' " +
                " property - " + className);
    }

}
