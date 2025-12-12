package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.persistence.TransactionRecord;
import com.jpmc.midascore.persistence.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRecordRepository txRepo;

    public TransactionService(UserRepository userRepository, TransactionRecordRepository txRepo) {
        this.userRepository = userRepository;
        this.txRepo = txRepo;
    }

    /**
     * Validate and persist the transaction. If validation fails, discard quietly.
     * Uses existing UserRepository (com.jpmc.midascore.repository.UserRepository)
     * which exposes findById(long) returning UserRecord (null if not found).
     */
    @Transactional
    public void process(Transaction tx) {
        if (tx == null) return;

        long senderId = tx.getSenderId();
        long recipientId = tx.getRecipientId();
        BigDecimal amount = BigDecimal.valueOf(tx.getAmount());

        // existing repository api: UserRecord findById(long id)
        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        if (sender == null || recipient == null) {
            return; // invalid ids -> discard
        }

        // Defensive null-balance handling (assumes UserRecord has getBalance()/setBalance(BigDecimal))
        BigDecimal senderBal = sender.getBalance() == null ? BigDecimal.ZERO : sender.getBalance();
        BigDecimal recipientBal = recipient.getBalance() == null ? BigDecimal.ZERO : recipient.getBalance();

        if (senderBal.compareTo(amount) >= 0) {
            sender.setBalance(senderBal.subtract(amount));
            recipient.setBalance(recipientBal.add(amount));
            userRepository.save(sender);
            userRepository.save(recipient);

            TransactionRecord record = new TransactionRecord(sender, recipient, amount);
            txRepo.save(record);
        }
    }
}
