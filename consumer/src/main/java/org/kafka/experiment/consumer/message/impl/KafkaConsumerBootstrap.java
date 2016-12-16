package org.kafka.experiment.consumer.message.impl;

import org.kafka.experiment.consumer.message.MessageConsumer;
import org.kafka.experiment.utils.CsvReport;
import org.kafka.experiment.utils.Tasks;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Start all consumers
 */
@Component
public class KafkaConsumerBootstrap {

    private static final Logger LOG = Logger.getLogger(KafkaConsumerBootstrap.class);

    @Autowired
    private Environment environment;

    @Autowired
    private CsvReport csvReport;

    public void bootstrap() {
        List<String> topics = environment.getProperty("app.kafka.consumer.topics", List.class);

        ExecutorService executor = Executors.newFixedThreadPool(topics.size());

        // Extract unique topics
        Set<String> uniqueTopics = new HashSet<>();
        topics.forEach(topic -> uniqueTopics.add(topic));

        ExecutorService orderVerifierExecutor = Executors.newFixedThreadPool(uniqueTopics.size());

        try {
            List<Future<?>> tasks = new ArrayList<>();
            topics.forEach(topic -> tasks.add(executor.submit(() -> {
                try {
                    MessageConsumer consumer = new KafkaConsoleLatencyMessageConsumer(topic);
                    consumer.consume();
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            })));

            uniqueTopics.forEach(topic ->
                    tasks.add(orderVerifierExecutor.submit(() -> {
                        try {
                            MessageConsumer consumer = new KafkaOrderVerifierConsoleMessageConsumer(topic);
                            consumer.consume();
                        } catch (Exception e) {
                            LOG.error(e.getMessage(), e);
                        }
                    })));

            Tasks.waitForAllFutures(tasks);

        } finally {
            executor.shutdown();
            orderVerifierExecutor.shutdown();
        }

        // Generate the latency report
        LOG.trace("start generating consumer latency report...");
        csvReport.generateReport("consumer-latency-report-" + UUID.randomUUID().toString() + ".csv");
        LOG.trace("start generating consumer latency report...done");
    }
}
