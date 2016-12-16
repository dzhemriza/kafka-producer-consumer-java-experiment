package org.kafka.experiment.consumer.message.impl;

import org.kafka.experiment.utils.CsvReport;
import org.kafka.experiment.utils.Message;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import scala.collection.mutable.StringBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Generic class for consuming messages from kafka and report latency metrics
 */
@Configurable
public class KafkaConsoleLatencyMessageConsumer extends KafkaTimedConsoleConsumerBase {

    private static Logger LOG = Logger.getLogger(KafkaConsoleLatencyMessageConsumer.class);

    private long minLatency = Integer.MAX_VALUE;
    private long maxLatency = Integer.MIN_VALUE;
    private long timeoutForStats;
    private long consolePrintTimeout;

    @Autowired
    private CsvReport csvReport;

    private List<Integer> report = new ArrayList<>();

    public KafkaConsoleLatencyMessageConsumer(String topic) {
        super(topic);
    }

    @Override
    protected void consumeRecord(ConsumerRecord<String, String> record) {
        Message msg;
        try {
            msg = objectMapper.readValue(record.value(), Message.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to deserialize Message: " + e.getMessage(), e);
        }

        // Track latency
        long timeOfReceivingData = System.currentTimeMillis();
        long latency = timeOfReceivingData - msg.getTime();

        if (latency < minLatency) {
            minLatency = latency;
        }

        if (maxLatency < latency) {
            maxLatency = latency;
        }

        if ((timeoutForStats + consolePrintTimeout) < System.currentTimeMillis()) {
            timeoutForStats = System.currentTimeMillis();
            LOG.info("Latency: min: " + minLatency + ", max: " + maxLatency + ", current: " + latency);
            report.add(Math.toIntExact(latency));
        }
    }

    @Override
    protected Consumer<String, String> createConsumer() {
        StringBuilder sb = new StringBuilder();

        sb.append(environment.getProperty("app.kafka.property.latency.consumer.group.id"));
        sb.append("-");
        sb.append(UUID.randomUUID().toString()); // group is unique to allow of consumption of all messages

        return consumerFactory.create(sb.toString());
    }

    protected void beforeConsume() {
        consolePrintTimeout = environment.getProperty("app.kafka.console.print.timeout", Long.class);
        timeoutForStats = System.currentTimeMillis();
    }

    @Override
    protected void afterConsume(Exception ex) {
        csvReport.addColumn("consumer-" + UUID.randomUUID().toString(), report);
    }
}
