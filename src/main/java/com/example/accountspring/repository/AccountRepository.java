package com.example.accountspring.repository;

import com.example.accountspring.domain.Account;
import com.example.accountspring.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findFirstByOrderByIdDesc();
    Integer countByAccountUser(AccountUser accountUser);
    Optional<Account> findByAccountNumber(String AccountNumber);
    List<Account> findByAccountUser(AccountUser accountUser);
}
