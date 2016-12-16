package org.kafka.experiment.consumer.message.impl;

import org.kafka.experiment.utils.Message;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.IOException;
import java.util.HashMap;

/**
 * Start consuming kafka messages and ensure that the order is preserved
 */
@Configurable
public class KafkaOrderVerifierConsoleMessageConsumer extends KafkaTimedConsoleConsumerBase {

    private static Logger LOG = Logger.getLogger(KafkaOrderVerifierConsoleMessageConsumer.class);

    private HashMap<Integer, Long> order = new HashMap<>();

    public KafkaOrderVerifierConsoleMessageConsumer(String topic) {
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

        if (order.containsKey(record.partition())) {
            long previousSequenceNumber = order.get(record.partition());

            if (previousSequenceNumber + 1 != msg.getSequenceNumber()) {
                // Out of order detected
                LOG.error("Out of order detected previous sequence number "
                        + previousSequenceNumber
                        + ", current sequence number "
                        + msg.getSequenceNumber()
                        + ", record data "
                        + record);
            }
        }

        order.put(record.partition(), msg.getSequenceNumber());
    }

    @Override
    protected Consumer<String, String> createConsumer() {
        String groupId = environment.getProperty("app.kafka.property.order.verifier.consumer.group.id");
        return consumerFactory.create(groupId);
    }

}
