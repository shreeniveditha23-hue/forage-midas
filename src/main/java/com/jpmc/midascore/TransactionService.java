package com.jpmc.midascore;

import com.jpmc.midascore.foundation.User;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.persistence.TransactionRecord;
import com.jpmc.midascore.persistence.TransactionRecordRepository;
import com.jpmc.midascore.persistence.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

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
     */
    @Transactional
    public void process(Transaction tx) {
        if (tx == null) return;

        Long senderId = Long.valueOf(tx.getSenderId());
        Long recipientId = Long.valueOf(tx.getRecipientId());
        BigDecimal amount = BigDecimal.valueOf(tx.getAmount());

        Optional<User> maybeSender = userRepository.findById(senderId);
        Optional<User> maybeRecipient = userRepository.findById(recipientId);

        if (maybeSender.isEmpty() || maybeRecipient.isEmpty()) {
            return;
        }

        User sender = maybeSender.get();
        User recipient = maybeRecipient.get();

        if (sender.getBalance() == null) sender.setBalance(BigDecimal.ZERO);
        if (recipient.getBalance() == null) recipient.setBalance(BigDecimal.ZERO);

        if (sender.getBalance().compareTo(amount) >= 0) {
            sender.setBalance(sender.getBalance().subtract(amount));
            recipient.setBalance(recipient.getBalance().add(amount));
            userRepository.save(sender);
            userRepository.save(recipient);

            TransactionRecord record = new TransactionRecord(sender, recipient, amount);
            txRepo.save(record);
        }
    }
}
