package org.kafka.experiment.consumer.message.impl;

import org.kafka.experiment.consumer.message.ConsumerFactory;
import org.kafka.experiment.utils.AppProperties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component
public class KafkaConsumerFactory implements ConsumerFactory {

    @Autowired
    private Environment environment;

    @Override
    public Consumer<String, String> create() {
        return new KafkaConsumer<>(readConsumerProperties());
    }

    @Override
    public Consumer<String, String> create(String groupId) {
        Properties properties = readConsumerProperties();

        properties.setProperty("group.id", groupId);

        return new KafkaConsumer<>(properties);
    }

    /**
     * @return Kafka consumer properties
     */
    private Properties readConsumerProperties() {
        Map<String, Object> allProps = AppProperties.getAllKnownProperties(environment);
        return AppProperties.filterKafkaProperties(allProps);
    }
}
