package com.account_bank.service;

import com.account_bank.form.TransactionForm;
import com.account_bank.model.Payment;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface PaymentSvc {

    Optional<Payment> findOne(Long paymentId);
    ResponseEntity<Object> getAll();

    ResponseEntity<Object> paymentSingle(TransactionForm form);

    Payment save(Payment Payment);

    void logHistTrans(Payment Payment);
    String transactionId();
}
