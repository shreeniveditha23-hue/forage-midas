package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, Transaction> template;

    public KafkaProducer(KafkaTemplate<String, Transaction> template) {
        this.template = template;
    }

    public void send(String topic, Transaction transaction) {
        template.send(topic, transaction);
    }
}
