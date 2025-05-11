package com.account_bank.service;

import com.account_bank.config.InvalidActionException;
import com.account_bank.config.ResponseMessage;
import com.account_bank.config.ResponseUtil;
import com.account_bank.form.BulkForm;
import com.account_bank.form.TransactionForm;
import com.account_bank.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class MakerSvcImpl implements MakerSvc {

    private PaymentSvc paymentSvc;

    @Override
    public ResponseEntity<Object> create(TransactionForm form) {
        try {
            return paymentSvc.paymentSingle(form);
        } catch (Exception e) {
            return ResponseUtil.build(ResponseMessage.INTERNAL_SERVER_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
