package com.account_bank.service;

import com.account_bank.model.Payment;

public interface PortfolioSvc {

    //TODO: Process From Kafka
    void paymentProcess(Payment payment);
}
