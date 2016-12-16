package org.kafka.experiment.producer.message.impl;

import org.kafka.experiment.producer.message.ProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Component
public class SharedKafkaRandomProducerBootstrapStrategy implements KafkaProducerBootstrapStrategy {

    @Autowired
    private ProducerFactory producerFactory;

    @Override
    public void bootstrap(List<ProducerConfig> producersConfigs, ExecutorService pool, List<Future<?>> tasks) {
        Producer<String, String> kafkaProducer = producerFactory.create();

        for (ProducerConfig config : producersConfigs) {
            Future<?> producer = pool.submit((Runnable) () -> {
                KafkaRandomMessageProducer randomMessageProducer =
                        new KafkaRandomMessageProducer(kafkaProducer, config);
                randomMessageProducer.produce();
            });

            tasks.add(producer);
        }
    }
}
