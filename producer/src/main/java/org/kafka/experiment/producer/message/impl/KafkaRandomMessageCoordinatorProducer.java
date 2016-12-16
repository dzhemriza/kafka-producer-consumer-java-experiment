package org.kafka.experiment.producer.message.impl;

import org.kafka.experiment.producer.message.MessageProducer;
import org.kafka.experiment.producer.message.ProducerFactory;
import org.kafka.experiment.producer.stats.TimeStats;
import org.kafka.experiment.utils.CsvReport;
import org.kafka.experiment.utils.Tasks;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Coordinates {@link KafkaRandomMessageProducer} message producing strategy
 */
@Component
public class KafkaRandomMessageCoordinatorProducer implements MessageProducer {

    private final static Logger LOG = Logger.getLogger(KafkaRandomMessageCoordinatorProducer.class);

    @Autowired
    private Environment environment;

    @Autowired
    private ProducerFactory producerFactory;

    @Autowired
    private KafkaProducerBootstrapStrategyFactory producerBootstrapStrategyFactory;

    @Autowired
    private TimeStats timeStats;

    @Autowired
    private CsvReport csvReportAllProducers;

    @Override
    public void produce() {
        LOG.trace("start");

        List<ProducerConfig> producersConfigs = extractProducerConfigs();

        LOG.trace("Number of producers: " + producersConfigs.size());

        // Each producer is running inside it own thread
        ExecutorService pool = Executors.newFixedThreadPool(producersConfigs.size());
        try {
            List<Future<?>> tasks = new ArrayList<>();

            KafkaProducerBootstrapStrategy strategy = producerBootstrapStrategyFactory.createStrategy();
            strategy.bootstrap(producersConfigs, pool, tasks);

            Tasks.waitForAllFutures(tasks);

            totalMessagesPerSecondReport();

            csvReportAllProducers.generateReport("all-producers-" + UUID.randomUUID().toString() + ".csv");

        } finally {
            pool.shutdown();
        }

        LOG.trace("end");
    }

    private void totalMessagesPerSecondReport() {
        CsvReport csvReport = new CsvReport();
        csvReport.addColumn("Total messages per second", timeStats.getReport());
        csvReport.generateReport("total-messages-per-second-" + UUID.randomUUID().toString() + ".csv");
    }

    /**
     * Extract producers configuration from property file
     *
     * @return {@link List} of {@link ProducerConfig}
     */
    private List<ProducerConfig> extractProducerConfigs() {
        List<ProducerConfig> result = new ArrayList<>();

        List<String> producersConfigs = environment.getProperty("app.kafka.producer.config", List.class);
        LOG.trace("Producers Configs: " + producersConfigs);

        for (String conf : producersConfigs) {
            String[] params = conf.split(":");

            if (params.length != 3) {
                throw new IllegalStateException("Expected data in form of "
                        + "topic_name:partition_number:number_of_messages");
            }

            ProducerConfig config = new ProducerConfig(params[0],
                    Integer.valueOf(params[1]), Integer.valueOf(params[2]));

            result.add(config);
        }

        return result;
    }
}
