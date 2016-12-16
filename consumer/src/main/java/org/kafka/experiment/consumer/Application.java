package org.kafka.experiment.consumer;

import org.kafka.experiment.consumer.message.impl.KafkaConsumerBootstrap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

/**
 * Kafka MessageConsumer Application
 */
@Configuration
@EnableSpringConfigured
@ComponentScan("org.kafka.experiment")
@PropertySource("file:${config}")
public class Application {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Application.class);
        KafkaConsumerBootstrap bootstrap = applicationContext.getBean(KafkaConsumerBootstrap.class);
        bootstrap.bootstrap();
    }

}
