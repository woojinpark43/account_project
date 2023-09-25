package com.example.accountspring.controller;

import com.example.accountspring.dto.CancelBalance;
import com.example.accountspring.dto.TransactionDto;
import com.example.accountspring.dto.UseBalance;
import com.example.accountspring.exception.AccountException;
import com.example.accountspring.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionContoller {
    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    ) {

        try {
            return UseBalance.Response.from(transactionService.useBalance(request.getUserId(),
                    request.getAccountNumber(), request.getAmount()));
        } catch (AccountException e) {
            log.error("Failed to use balance. ");

            transactionService.saveFailedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }

    @PostMapping("/transaction/cancel")
    public CancelBalance.Response useBalance(
            @Valid @RequestBody CancelBalance.Request request
    ) {

        try {
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(request.getTransactionId(),
                    request.getAccountNumber(), request.getAmount()));
        } catch (AccountException e) {
            log.error("Failed to use balance. ");

            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }
}
