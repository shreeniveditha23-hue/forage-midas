package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ReceivedTransactionStore {

    private final List<Transaction> received = new CopyOnWriteArrayList<>();

    public void add(Transaction tx) {
        received.add(tx);
    }

    public List<Transaction> getAll() {
        return received;
    }
}
