package org.kafka.experiment.producer.stats;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeStats {

    private static final Logger LOG = Logger.getLogger(TimeStats.class);

    @Autowired
    @Qualifier("messagesPerSecond")
    private AtomicInteger messagesPerSecond;

    @Autowired
    @Qualifier("bestMessagesPerSecond")
    private AtomicInteger best;

    private List<Integer> report = new ArrayList<>();

    public void incrementTotalMessagesPerSecond() {
        messagesPerSecond.incrementAndGet();
    }

    public List<Integer> getReport() {
        return report;
    }

    public void showStatsPerSeconds() {
        int old = messagesPerSecond.getAndSet(0);

        LOG.debug("Message rate: " + old);
        if (old > best.get()) {
            best.set(old);
        }

        synchronized (report) {
            report.add(old);
        }
    }

    public void showBestStats() {
        LOG.debug("Best message rate: " + best.get());
    }
}
