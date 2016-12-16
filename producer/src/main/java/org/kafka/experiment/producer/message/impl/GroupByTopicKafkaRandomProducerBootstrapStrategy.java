package org.kafka.experiment.producer.message.impl;

import org.kafka.experiment.producer.message.ProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Component
public class GroupByTopicKafkaRandomProducerBootstrapStrategy implements KafkaProducerBootstrapStrategy {

    @Autowired
    private ProducerFactory producerFactory;

    @Override
    public void bootstrap(List<ProducerConfig> producersConfigs, ExecutorService pool, List<Future<?>> tasks) {
        Map<String, List<ProducerConfig>> groupedConfigs = new HashMap<>();

        // Group configs
        for (ProducerConfig config : producersConfigs) {

            if (groupedConfigs.containsKey(config.getTopic())) {
                List<ProducerConfig> configGroup = groupedConfigs.get(config.getTopic());
                configGroup.add(config);
            } else {
                List<ProducerConfig> configGroup = new ArrayList<>();
                configGroup.add(config);
                groupedConfigs.put(config.getTopic(), configGroup);
            }
        }

        for (String topic : groupedConfigs.keySet()) {
            List<ProducerConfig> configGroup = groupedConfigs.get(topic);

            Producer<String, String> producer = producerFactory.create();

            for (ProducerConfig config : configGroup) {
                Future<?> producerTask = pool.submit((Runnable) () -> {
                    // Workflow:
                    // - Create KafkaRandomMessageProducer
                    // - Start producing messages
                    KafkaRandomMessageProducer randomMessageProducer =
                            new KafkaRandomMessageProducer(producer, config);
                    randomMessageProducer.produce();
                });

                tasks.add(producerTask);
            }
        }
    }
}
