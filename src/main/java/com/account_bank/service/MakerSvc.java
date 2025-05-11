package com.account_bank.service;

import com.account_bank.form.TransactionForm;
import org.springframework.http.ResponseEntity;

public interface MakerSvc {

    ResponseEntity<Object> create(TransactionForm form);
}
