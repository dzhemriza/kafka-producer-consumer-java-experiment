package org.kafka.experiment.producer.message.impl;

import org.kafka.experiment.producer.message.ProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Component
public class SingleKafkaRandomProducerBootstrapStrategy implements KafkaProducerBootstrapStrategy {

    @Autowired
    private ProducerFactory producerFactory;

    @Override
    public void bootstrap(List<ProducerConfig> producersConfigs, ExecutorService pool, List<Future<?>> tasks) {
        for (ProducerConfig config : producersConfigs) {
            Future<?> producer = pool.submit((Runnable) () -> {
                // Workflow:
                // - Create KafkaRandomMessageProducer
                // - Start producing messages
                KafkaRandomMessageProducer randomMessageProducer =
                        new KafkaRandomMessageProducer(producerFactory.create(), config);
                randomMessageProducer.produce();
            });

            tasks.add(producer);
        }
    }
}
