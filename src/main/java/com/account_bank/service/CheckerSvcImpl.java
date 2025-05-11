package com.account_bank.service;

import com.account_bank.config.DataNotFoundException;
import com.account_bank.config.InvalidActionException;
import com.account_bank.config.ResponseMessage;
import com.account_bank.config.ResponseUtil;
import com.account_bank.cons.Status;
import com.account_bank.cons.StatusAuthorization;
import com.account_bank.kafka.ProducerSvc;
import com.account_bank.model.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CheckerSvcImpl implements CheckerSvc {
    private static final Logger logger = LoggerFactory.getLogger(CheckerSvcImpl.class);

    private PaymentSvc paymentSvc;
    private ProducerSvc producerSvc;

    @Override
    public ResponseEntity<Object> approve(Long paymentId) {
        try {
            Payment transaction = paymentSvc.findOne(paymentId).orElseThrow(() -> new DataNotFoundException("Transaction Not Found"));
            if (!transaction.getStatusAuthorization().equals(StatusAuthorization.SUBMIT)) {
                return ResponseUtil.build("transaction status must be SUBMIT", transaction, HttpStatus.BAD_REQUEST);
            }
            String msg = new ObjectMapper().writeValueAsString(transaction);
            transaction.setStatusAuthorization(StatusAuthorization.APPROVE);
            producerSvc.sendKafka(msg);
            paymentSvc.save(transaction);
            return ResponseUtil.build("Payment process", transaction, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("error sending to kafka -> " + e);
            return ResponseUtil.build(ResponseMessage.INTERNAL_SERVER_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> reject(Long paymentId) {
        Payment transaction = paymentSvc.findOne(paymentId).orElseThrow(() -> new DataNotFoundException("Transaction Not Found"));
        if (!transaction.getStatusAuthorization().equals(StatusAuthorization.SUBMIT)) {
            return ResponseUtil.build("transaction status must be SUBMIT", transaction, HttpStatus.BAD_REQUEST);
        }
        transaction.setStatusAuthorization(StatusAuthorization.REJECT);
        transaction.setStatusTransaction(Status.FAILED);
        paymentSvc.save(transaction);
        return ResponseUtil.build("Success Reject Payment", transaction, HttpStatus.OK);
    }
}
