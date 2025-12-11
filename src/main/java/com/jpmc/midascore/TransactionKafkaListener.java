package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionKafkaListener.class);

    private final ReceivedTransactionStore store;

    public TransactionKafkaListener(ReceivedTransactionStore store) {
        this.store = store;
    }

    @KafkaListener(topics = "${general.kafka-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(Transaction transaction) {
        log.info("Received transaction: {}", transaction);
        store.add(transaction);
    }
}
