package com.jpmc.midascore.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRecordRepository extends JpaRepository<com.jpmc.midascore.persistence.TransactionRecord, Long> {
}
