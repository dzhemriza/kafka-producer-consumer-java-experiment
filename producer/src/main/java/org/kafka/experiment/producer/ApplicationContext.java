package org.kafka.experiment.producer;

import org.kafka.experiment.producer.stats.TimeStats;
import org.kafka.experiment.utils.CsvReport;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Application Context
 */
@Configuration
public class ApplicationContext {

    @Autowired
    private Environment environment;

    @Bean
    public ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }

    @Bean(name = "messagesPerSecond")
    public AtomicInteger messagesPerSecond() {
        return new AtomicInteger(0);
    }

    @Bean(name = "bestMessagesPerSecond")
    public AtomicInteger bestValue() {
        return new AtomicInteger();
    }

    @Bean(name = "timeTrackingScheduler")
    public ScheduledExecutorService timeTrackingScheduler() {
        return Executors.newScheduledThreadPool(
                environment.getProperty(
                        "app.time.tracking.scheduler.threads",
                        Integer.class));
    }

    @Bean
    public TimeStats timeStats() {
        return new TimeStats();
    }

    @Bean
    public CsvReport csvReport() {
        return new CsvReport();
    }
}
