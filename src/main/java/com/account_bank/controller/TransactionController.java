package com.account_bank.controller;

import com.account_bank.model.Payment;
import com.account_bank.model.PaymentDetails;
import com.account_bank.service.PaymentSvc;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/transaction")
@AllArgsConstructor
public class TransactionController {

    private PaymentSvc paymentSvc;

    @GetMapping
    public ResponseEntity<Object> getTransactions() {
        return paymentSvc.getAll();
    }
}
