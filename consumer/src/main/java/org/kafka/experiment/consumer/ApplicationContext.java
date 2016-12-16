package org.kafka.experiment.consumer;

import org.kafka.experiment.utils.CsvReport;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application Context
 */
@Configuration
public class ApplicationContext {

    @Bean
    public ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CsvReport csvReport() {
        return new CsvReport();
    }
}
