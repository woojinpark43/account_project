package com.example.accountspring.service;

import com.example.accountspring.domain.Account;
import com.example.accountspring.domain.AccountUser;
import com.example.accountspring.domain.Transaction;
import com.example.accountspring.dto.TransactionDto;
import com.example.accountspring.exception.AccountException;
import com.example.accountspring.repository.AccountRepository;
import com.example.accountspring.repository.AccountUserRepository;
import com.example.accountspring.repository.TransactionRepository;
import com.example.accountspring.type.AccountStatus;
import com.example.accountspring.type.ErrorCode;
import com.example.accountspring.type.TransactionResultType;
import com.example.accountspring.type.TransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(()->new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()->new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateUseBalance(user, account, amount);

        account.useBalance(amount);
        Transaction transaction = saveAndGetTransaction(TransactionType.USE ,amount, account, TransactionResultType.S);

        return TransactionDto.fromEntity(transaction);
    }

    private void validateUseBalance(AccountUser user, Account account, Long amount) {
        if(!Objects.equals(user.getId(), account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }
        if(account.getAccountStatus() != AccountStatus.IN_USE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account  = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(TransactionType.USE, amount, account, TransactionResultType.F);
    }

    private Transaction saveAndGetTransaction(
            TransactionType transactionType,
            Long amount,
            Account account,
            TransactionResultType transactionResultType
    ) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResult(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }

   @Transactional
    public TransactionDto cancelBalance(String transactionId, String accountNumber, Long amount) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(()->new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));
        Account account  = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));

        validateCancelBalance(transaction, account, amount);

        account.useBalance(amount);

        return TransactionDto.fromEntity(
                saveAndGetTransaction(TransactionType.CANCEL, amount, account, TransactionResultType.S)
        );
    }

    private void validateCancelBalance(Transaction transaction, Account account, Long amount) {
        if(!Objects.equals(transaction.getAccount().getId(), account.getId())) {
            throw new AccountException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if(!Objects.equals(transaction.getAmount(), amount)){
            throw new AccountException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if(transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))){
            throw new AccountException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
    }

    @Transactional
    public void saveFailedCancelTransaction(String accountNumber, Long amount) {
        Account account  = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));
        saveAndGetTransaction(TransactionType.CANCEL, amount, account, TransactionResultType.F);
    }
}
