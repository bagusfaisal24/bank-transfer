package com.account_bank.service;

import com.account_bank.config.ResponseMessage;
import com.account_bank.config.ResponseUtil;
import com.account_bank.cons.StatusAuthorization;
import com.account_bank.form.TransactionForm;
import com.account_bank.model.*;
import com.account_bank.repository.*;
import com.account_bank.utils.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentSvcImpl implements PaymentSvc {
    private static final Logger logger = LoggerFactory.getLogger(CheckerSvcImpl.class);

    private AccountBankRepository accountBankRepo;
    private PortfolioAccountRepository portfolioAccountRepo;
    private PaymentRepository paymentRepository;
    private PaymentDetailsRepository paymentDetailsRepo;
    private PlatformTransactionManager platformTransactionManager;
    private LogHistTransRepository logRepo;

    @Override
    public Optional<Payment> findOne(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public ResponseEntity<Object> getAll() {
        try {
            List<Payment> payments = (List<Payment>) paymentRepository.findAll();
            return ResponseUtil.build("success get payments", payments, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.build(ResponseMessage.INTERNAL_SERVER_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> paymentSingle(TransactionForm form) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("create_payment");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus tStatus = platformTransactionManager.getTransaction(def);
        Payment payment;
        try {
            logger.warn("New Request -> " + new ObjectMapper().writeValueAsString(form));
            AccountBank source = accountBankRepo.findByAccount_number(form.getSourceAccount());
            AccountBank destination = accountBankRepo.findByAccount_number(form.getDestinationAccount());
            if (source == null)
                return ResponseUtil.build("Source account data : Not Found", null, HttpStatus.NOT_FOUND);
            if (destination == null)
                return ResponseUtil.build("Destination account data : Not Found", null, HttpStatus.NOT_FOUND);
            PortfolioAccount portfolioAccount = portfolioAccountRepo.findPortfolioAccountByAccountBank(source.getId());
            if (form.getAmount() > portfolioAccount.getAmount())
                return ResponseUtil.build("Insufficent Funds", null, HttpStatus.BAD_REQUEST);
            payment = Payment.builder()
                    .transactionId(transactionId())
                    .totalAmount(form.getAmount())
                    .transactionType(form.getTransactionType().name())
                    .statusAuthorization(StatusAuthorization.SUBMIT)
                    .build();
            payment = save(payment);
            PaymentDetails paymentDetails = PaymentDetails.builder()
                    .payment(payment)
                    .amount(form.getAmount())
                    .sourceAccount(form.getSourceAccount())
                    .destinationAccount(form.getDestinationAccount())
                    .build();
            paymentDetailsRepo.save(paymentDetails);
        } catch (Exception e) {
            logger.error("error create payment " + e);
            platformTransactionManager.rollback(tStatus);
            return ResponseUtil.build(ResponseMessage.INTERNAL_SERVER_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        platformTransactionManager.commit(tStatus);
        return ResponseUtil.build("Success create payment", payment, HttpStatus.CREATED);
    }

    @Override
    public void logHistTrans(Payment payment) {
        LogHistTrans logHistTrans = LogHistTrans.builder()
                .transactionId(payment.getTransactionId())
                .statusTransaction(payment.getStatusTransaction())
                .statusAuthorization(payment.getStatusAuthorization())
                .build();
        logRepo.save(logHistTrans);
    }

    @Override
    public Payment save(Payment Payment) {
        logHistTrans(Payment);
        return paymentRepository.save(Payment);
    }

    public String transactionId() {
        String FORMAT = "%s-%s";
        return String.format(FORMAT, UUID.randomUUID().toString().substring(0, 3), DateUtils.getDateCustom());
    }
}
