package org.kafka.experiment.consumer.message.impl;

import org.kafka.experiment.consumer.message.ConsumerFactory;
import org.kafka.experiment.consumer.message.MessageConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for the consumers
 */
@Configurable
public abstract class KafkaTimedConsoleConsumerBase implements MessageConsumer {

    private static final Logger LOG = Logger.getLogger(KafkaTimedConsoleConsumerBase.class);

    @Autowired
    protected Environment environment;

    @Autowired
    protected ConsumerFactory consumerFactory;

    @Autowired
    protected ObjectMapper objectMapper;

    protected List<String> topics = new ArrayList<>();

    protected long totalConsumed = 0;

    public KafkaTimedConsoleConsumerBase(String topic) {
        topics.add(topic);
    }

    @Override
    public void consume() {
        Exception consumptionError = null;

        try {
            Consumer<String, String> consumer = createConsumer();

            consumer.subscribe(topics);

            beforeConsume();

            consumeImpl(consumer);

            LOG.info("Total number of consumed messages " + totalConsumed);

        } catch (Exception e) {
            consumptionError = e;
            LOG.error(e.getMessage(), e);
            throw e; // re-throw
        } finally {
            afterConsume(consumptionError);
        }
    }

    /**
     * Consume record
     *
     * @param record
     */
    protected abstract void consumeRecord(ConsumerRecord<String, String> record);

    /**
     * Method used to create a {@link Consumer} by derived class policy
     *
     * @return
     */
    protected abstract Consumer<String, String> createConsumer();

    /**
     * Before actual consumption is started
     */
    protected void beforeConsume() {
        // Do nothing
    }

    /**
     * Indicates when the consumption of the messages is over
     *
     * @param error
     */
    protected void afterConsume(Exception error) {
        // Do nothing
    }

    /**
     * Consume messages and stops after certain amout of time
     *
     * @param messageReader
     */
    private void consumeImpl(Consumer<String, String> messageReader) {
        int pollTimeout = environment.getProperty("app.kafka.pull.timeout", Integer.class);
        long timeoutNoMessages = environment.getProperty("app.kafka.timeout.no.messages", Long.class);

        long timeoutNoMessagesMark = System.currentTimeMillis();
        while (true) {
            ConsumerRecords<String, String> records = messageReader.poll(pollTimeout);

            if (records.count() == 0) {
                if ((timeoutNoMessagesMark + timeoutNoMessages) < System.currentTimeMillis()) {
                    LOG.debug("Timeout after " + timeoutNoMessages + " milliseconds, terminating.");
                    break;
                }

                continue;
            }

            timeoutNoMessagesMark = System.currentTimeMillis();

            for (ConsumerRecord<String, String> record : records) {
                ++totalConsumed;

                consumeRecord(record);
            }
        }
    }
}
