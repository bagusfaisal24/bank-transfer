package com.account_bank.service;

import org.springframework.http.ResponseEntity;

public interface CheckerSvc {
    //TODO : Send to kafka
    ResponseEntity<Object> approve(Long paymentId);
    ResponseEntity<Object> reject(Long paymentId);
}
