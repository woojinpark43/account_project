package com.example.accountspring.controller;

import com.example.accountspring.domain.Account;
import com.example.accountspring.dto.AccountDto;
import com.example.accountspring.dto.DeleteAccount;
import com.example.accountspring.service.AccountService;
import com.example.accountspring.service.RedisTestService;
import com.example.accountspring.type.AccountStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @MockBean
    private RedisTestService redisTestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successGetAccount() throws Exception {
        given(accountService.getAccount(anyLong()))
                .willReturn(Account.builder()
                        .accountNumber("3456")
                        .accountStatus(AccountStatus.IN_USE)
                        .build());

        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber").value("3456"))
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());
    }

    @Test
    void successGetAccountByUserId() throws Exception {
        List<AccountDto> accountDtos = Arrays.asList(
                AccountDto.builder()
                .accountNumber("1234567890")
                .balance(1000L)
                .build(),
                AccountDto.builder()
                        .accountNumber("1111111111")
                        .balance(1000L)
                        .build(),
                AccountDto.builder()
                        .accountNumber("2222222222")
                        .balance(1000L)
                        .build());
        given(accountService.getAccountsByUserId(anyLong()))
                .willReturn(accountDtos);

        mockMvc.perform(get("/account?user_id=1"))
                .andDo(print())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].balance").value(1000L))
                .andExpect(status().isOk());
    }

    @Test
    void successDeleteAccount() throws Exception {
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());

        mockMvc.perform(delete("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new DeleteAccount.Request(3333L, "1234567890")
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("IN_USE"))
                .andDo(print());
    }

}