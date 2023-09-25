package com.example.accountspring.repository;

import com.example.accountspring.domain.Account;
import com.example.accountspring.domain.AccountUser;
import com.example.accountspring.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);
}
