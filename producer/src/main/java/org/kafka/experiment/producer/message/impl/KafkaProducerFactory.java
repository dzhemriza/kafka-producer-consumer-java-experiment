package org.kafka.experiment.producer.message.impl;

import org.kafka.experiment.producer.message.ProducerFactory;
import org.kafka.experiment.utils.AppProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * Factory implementation
 */
@Component
public class KafkaProducerFactory implements ProducerFactory {

    @Autowired
    Environment environment;

    @Override
    public Producer<String, String> create() {
        return new KafkaProducer<>(readProducerProperties());
    }

    /**
     * @return Kafka producer properties
     */
    private Properties readProducerProperties() {
        Map<String, Object> allProps = AppProperties.getAllKnownProperties(environment);
        return AppProperties.filterKafkaProperties(allProps);
    }
}
