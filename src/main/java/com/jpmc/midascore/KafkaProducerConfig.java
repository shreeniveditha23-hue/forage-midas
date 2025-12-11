package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean
    public DefaultKafkaProducerFactory<String, Transaction> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        // Resolve bootstrap from embedded Kafka or Spring config
        String bootstrap = env.getProperty("spring.kafka.bootstrap-servers");
        if (bootstrap == null || bootstrap.isBlank()) {
            bootstrap = env.getProperty("spring.embedded.kafka.brokers");
        }
        if (bootstrap != null) {
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        }

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Transaction> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
