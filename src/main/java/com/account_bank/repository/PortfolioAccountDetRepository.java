package com.account_bank.repository;

import com.account_bank.model.PortfolioAccount;
import com.account_bank.model.PortfolioAccountDet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PortfolioAccountDetRepository extends CrudRepository<PortfolioAccountDet, Long> {
    @Query(value = "select * from PORTFOLIO_DET where deleted_at is null and ACCOUNT_ID =?1",
            nativeQuery = true)
    List<PortfolioAccountDet> findPortfolioAccountByAccountBank(Long accountId);
}
