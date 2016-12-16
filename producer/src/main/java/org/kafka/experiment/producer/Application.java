package org.kafka.experiment.producer;

import org.kafka.experiment.producer.message.MessageProducer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Application main code
 */
@Configuration
@EnableSpringConfigured
@ComponentScan("org.kafka.experiment")
@PropertySource("file:${config}")
public class Application {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Application.class);

        ScheduledExecutorService timeTrackingScheduler =
                applicationContext.getBean("timeTrackingScheduler",
                        ScheduledExecutorService.class);

        try {
            MessageProducer producer = applicationContext.getBean(MessageProducer.class);
            producer.produce();

        } finally {
            timeTrackingScheduler.shutdown();
        }
    }
}
