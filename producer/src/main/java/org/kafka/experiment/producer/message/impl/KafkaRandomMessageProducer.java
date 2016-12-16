package org.kafka.experiment.producer.message.impl;

import org.kafka.experiment.producer.message.MessageProducer;
import org.kafka.experiment.producer.message.ProducerFactory;
import org.kafka.experiment.producer.stats.TimeStats;
import org.kafka.experiment.utils.CsvReport;
import org.kafka.experiment.utils.Message;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.env.Environment;
import scala.collection.mutable.StringBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * Kafka message producer
 * Note: Each {@link KafkaRandomMessageProducer} runs in its own thread
 */
@Configurable
public class KafkaRandomMessageProducer implements MessageProducer {

    private static Logger LOG = Logger.getLogger(KafkaRandomMessageProducer.class);

    @Autowired
    private Environment environment;

    @Autowired
    private ProducerFactory producerFactory;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private TimeStats timeStats;

    private Random random = new Random();

    private Producer<String,String> producer;

    private long sequenceNumber = 0;

    private final ProducerConfig producerConfig;

    private int messagesPerSecondLocal;
    private long lastCheck;
    private List<Integer> report = new ArrayList<>();

    @Autowired
    private CsvReport csvReport;

    public KafkaRandomMessageProducer(Producer<String,String> producer, ProducerConfig producerConfig) {
        this.producer = producer;
        this.producerConfig = producerConfig;
        this.messagesPerSecondLocal = 0;
    }

    @Override
    public void produce() {
        LOG.info("start");
        boolean waitForAck = environment.getProperty("app.kafka.wait.for.ack", Boolean.class);

        lastCheck = System.currentTimeMillis();

        for (int i = 0; i < this.producerConfig.getNumberOfMessages(); ++i) {
            String msg = null;
            try {
                boolean isLast = (i + 1 == this.producerConfig.getNumberOfMessages());
                msg = createRandomMessage(isLast);
            } catch (IOException e) {
                LOG.error("error while generating a random message");
                continue;
            }

            Future<RecordMetadata> sendFuture = producer.send(
                    new ProducerRecord<>(this.producerConfig.getTopic(),
                            this.producerConfig.getPartition(), null, msg));

            if (waitForAck) {
                if (!isDelivered(sendFuture)) {
                    LOG.error("Unable to deliver request! Topic: " + this.producerConfig.getTopic() + ", Msg: " + msg);
                }
            }

            trackTime();
        }

        csvReport.addColumn("producer-" + UUID.randomUUID().toString(), report);

        LOG.info("end");
    }

    /**
     * Track time statistics for general publishing and current producer publishing
     */
    private void trackTime() {
        timeStats.incrementTotalMessagesPerSecond();
        messagesPerSecondLocal++; // thread safe

        final long ONE_SEC_IN_MILLIS = 1000;
        if (lastCheck + ONE_SEC_IN_MILLIS <= System.currentTimeMillis()) {
            lastCheck = System.currentTimeMillis();

            // report
            LOG.info("Producer messages rate per second: " + messagesPerSecondLocal);
            report.add(messagesPerSecondLocal);

            // reset
            messagesPerSecondLocal = 0;
        }
    }

    /**
     * @param sendFuture
     * @return {@code true} in case of delivery success {@code false} otherwise
     */
    private boolean isDelivered(Future<RecordMetadata> sendFuture) {
        try {
            // Wait until future timeouts or future is delivered
            sendFuture.get();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     * @return Random unique message containing UUID and time
     * @param isLast
     */
    private String createRandomMessage(boolean isLast) throws IOException {
        Message message = randomMessage(nextSequenceNumber(), isLast);
        return jsonMapper.writeValueAsString(message);
    }

    /**
     * @return Generated {@link Message} class with random data
     * @param isLast
     */
    private Message randomMessage(long sequenceNumber, boolean isLast) {
        int maxChars = environment.getProperty("app.random.message.max.characters", Integer.class);
        int numberOfChars = random.nextInt(maxChars);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfChars; ++i) {
            sb.append("x");
        }

        return new Message(UUID.randomUUID(),
                System.currentTimeMillis(),
                sequenceNumber,
                sb.toString(),
                isLast);
    }

    /**
     * @return Next avaliable sequence number
     */
    private long nextSequenceNumber() {
        return ++sequenceNumber;
    }

    public List<Integer> getReport() {
        return report;
    }
}
