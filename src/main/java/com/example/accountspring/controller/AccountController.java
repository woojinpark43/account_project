package com.example.accountspring.controller;

import com.example.accountspring.dto.AccountInfo;
import com.example.accountspring.dto.CreateAccount;
import com.example.accountspring.dto.DeleteAccount;
import com.example.accountspring.service.AccountService;
import com.example.accountspring.service.RedisTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
    ) {
        accountService.createAccount(
                request.getUserId(),
                request.getInitialBalance());
        return CreateAccount.Response.from(
                accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance()
                )
        );
    }

    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request
    ) {
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId(
            @RequestParam("user_id") Long userId
    ){
        return accountService.getAccountsByUserId(userId)
                .stream().map(accountDto -> AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance())
                        .build())
                .collect(Collectors.toList());
    }
//    @GetMapping("/get-lock")
//    public String getLock() {
//        return redisTestService.getLock();
//    }
//
//    @GetMapping("/create-account")
//    public String createAccount(){
////        accountService.createAccount();
//        return "success";
//    }
//
//    @GetMapping("/account/{id}")
//    public Account getAccount(
//            @PathVariable Long id) {
//        return accountService.getAccount(id);
//    }
}
