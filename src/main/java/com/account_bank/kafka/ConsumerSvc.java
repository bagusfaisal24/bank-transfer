package com.account_bank.kafka;

import com.account_bank.cons.Status;
import com.account_bank.cons.StatusAuthorization;
import com.account_bank.model.Payment;
import com.account_bank.service.PaymentSvc;
import com.account_bank.service.PortfolioSvc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ConsumerSvc {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerSvc.class);

    private PortfolioSvc portfolioSvc;
    private PaymentSvc paymentSvc;

    @KafkaListener(topics = TopicConstant.PAYMENT, groupId = "PAYMENT")
    public void listenMessage(ConsumerRecord<String, Object> record) throws JsonProcessingException {
        if (record.key().equalsIgnoreCase("msg")) {
            logger.warn("incoming message -> " + record.value());
            Payment payment = new ObjectMapper().readValue(record.value().toString(), Payment.class);
            Optional<Payment> optpayment = paymentSvc.findOne(payment.getId());
            if (!optpayment.isPresent()) {
                logger.error("payment id not found -> " + payment.getTransactionId());
            } else {
                payment.setStatusAuthorization(StatusAuthorization.IN_PROGRESS);
                paymentSvc.save(payment);
                portfolioSvc.paymentProcess(payment);
            }
        }
    }
}
