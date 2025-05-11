package com.account_bank.repository;

import com.account_bank.model.AccountBank;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AccountBankRepository extends CrudRepository<AccountBank, Long> {

    @Query(value = "select * from ACCOUNT_BANK where ACCOUNT_NUMBER =?1",
            nativeQuery = true)
    AccountBank findByAccount_number(String accountNumber);
}
