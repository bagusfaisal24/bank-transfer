package com.account_bank.controller;

import com.account_bank.model.AccountBank;
import com.account_bank.repository.AccountBankRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/account")
@AllArgsConstructor
public class AccountController {
    private AccountBankRepository accountBankRepository;

    @GetMapping()
    public List<AccountBank> getList(){
        return (List<AccountBank>) accountBankRepository.findAll();
    }
}
