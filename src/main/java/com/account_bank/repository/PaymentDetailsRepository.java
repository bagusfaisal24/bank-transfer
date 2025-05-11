package com.account_bank.repository;

import com.account_bank.model.PaymentDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PaymentDetailsRepository extends CrudRepository<PaymentDetails, Long> {
    @Query(value = "select * from payment_details where deleted_at is null and payment_id =?1",
            nativeQuery = true)
    List<PaymentDetails> paymentByPayId(Long paymentID);
}
