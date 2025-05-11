package com.account_bank.repository;

import com.account_bank.model.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PaymentRepository extends CrudRepository<Payment, Long> {

}
