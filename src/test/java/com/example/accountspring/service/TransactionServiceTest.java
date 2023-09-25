package com.example.accountspring.service;

import com.example.accountspring.domain.Account;
import com.example.accountspring.domain.AccountUser;
import com.example.accountspring.domain.Transaction;
import com.example.accountspring.dto.TransactionDto;
import com.example.accountspring.repository.AccountRepository;
import com.example.accountspring.repository.AccountUserRepository;
import com.example.accountspring.repository.TransactionRepository;
import com.example.accountspring.type.AccountStatus;
import com.example.accountspring.type.TransactionResultType;
import com.example.accountspring.type.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void successUseBalance(){
        //given
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("Pobi")
                .build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(TransactionType.USE)
                        .transactionResult(TransactionResultType.S)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapshot(9000L)
                        .build());
        //when
        TransactionDto transactionDto = transactionService.useBalance(1L,
                "1000000000", 1000L);
        //then
        assertEquals(TransactionResultType.S, transactionDto.getTransactionResult());
        assertEquals(TransactionType.USE, transactionDto.getTransactionType());
        assertEquals(9000L, transactionDto.getBalanceSnapshot());
        assertEquals(1000L, transactionDto.getAmount());
    }
}