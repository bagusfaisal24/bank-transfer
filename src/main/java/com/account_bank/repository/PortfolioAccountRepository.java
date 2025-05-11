package com.account_bank.repository;

import com.account_bank.model.PortfolioAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PortfolioAccountRepository extends CrudRepository<PortfolioAccount, Long> {

    @Query(value = "select * from PORTFOLIO where deleted_at is null and ACCOUNT_ID =?1",
            nativeQuery = true)
    PortfolioAccount findPortfolioAccountByAccountBank(Long accountId);
}
