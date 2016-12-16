package org.kafka.experiment.utils;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AppProperties {

    private static final String KAFKA_PREFIX = "kafka.";

    /**
     * Read all spring provided (external and internal) properties and creates a {@link Map} representation.
     *
     * @param env
     * @return
     */
    public static Map<String, Object> getAllKnownProperties(Environment env) {
        Map<String, Object> rtn = new HashMap<>();
        if (env instanceof ConfigurableEnvironment) {
            for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env).getPropertySources()) {
                if (propertySource instanceof EnumerablePropertySource) {
                    for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
                        rtn.put(key, propertySource.getProperty(key));
                    }
                }
            }
        }
        return rtn;
    }

    /**
     * Helper method used to filter out all kafka properties
     *
     * @param props
     * @return
     */
    public static Properties filterKafkaProperties(Map<String, Object> props) {
        return filterByPrefix(props, KAFKA_PREFIX);
    }

    /**
     * Helper method that filters our a {@link Map} key represented as {@link String} by given prefix
     *
     * @param props
     * @param prefix
     * @return
     */
    public static Properties filterByPrefix(Map<String, Object> props, String prefix) {
        Properties properties = new Properties();

        props.keySet().stream().filter(
                key -> key.startsWith(prefix))
                .forEach(
                        key -> properties.put(
                                key.substring(prefix.length()), props.get(key)));

        return properties;
    }
}
