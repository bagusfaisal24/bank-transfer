package com.account_bank.service;

import com.account_bank.config.DataNotFoundException;
import com.account_bank.cons.Status;
import com.account_bank.cons.StatusAuthorization;
import com.account_bank.model.*;
import com.account_bank.repository.AccountBankRepository;
import com.account_bank.repository.PaymentDetailsRepository;
import com.account_bank.repository.PortfolioAccountDetRepository;
import com.account_bank.repository.PortfolioAccountRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PortfolioSvcImpl implements PortfolioSvc {
    private static final Logger logger = LoggerFactory.getLogger(PortfolioSvcImpl.class);

    private AccountBankRepository accountBankRepo;
    private PortfolioAccountRepository portoRepo;
    private PortfolioAccountDetRepository portoDetRepo;
    private PlatformTransactionManager platformTransactionManager;
    private PaymentSvc paymentSvc;
    private PaymentDetailsRepository paymentDetailsRepo;

    @Override
    public void paymentProcess(Payment msg) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("create_payment");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus tStatus = platformTransactionManager.getTransaction(def);
        try {
            logger.warn("read payment from message with payment id-> " + msg.getTransactionId());
            List<PaymentDetails> details = paymentDetailsRepo.paymentByPayId(msg.getId());
            for (PaymentDetails det : details) {
                debet(det);
                credit(det);
            }
            msg.setStatusAuthorization(StatusAuthorization.COMPLETED);
            msg.setStatusTransaction(Status.SUCCESS);
            paymentSvc.save(msg);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error process payment " + e);
            platformTransactionManager.rollback(tStatus);
        }
        platformTransactionManager.commit(tStatus);
        logger.warn("read payment from message with success process -> " + msg.getTransactionId());
    }

    private void debet(PaymentDetails paymentDetails) {
        AccountBank source = accountBankRepo.findByAccount_number(paymentDetails.getSourceAccount());
        if (source == null) throw new DataNotFoundException("Source account data : Not Found");
        String remarks = "Debet amount from account " + source.getAccount_holder();
        setPortfolio(PortfolioAccountDet.DEBET, source, remarks, paymentDetails);
    }


    private void credit(PaymentDetails paymentDetails) {
        AccountBank destination = accountBankRepo.findByAccount_number(paymentDetails.getDestinationAccount());
        if (destination == null) throw new DataNotFoundException("Destination account data : Not Found");
        String remarks = "Credit amount to account " + destination.getAccount_holder();
        setPortfolio(PortfolioAccountDet.KREDIT, destination, remarks, paymentDetails);
    }

    private void setPortfolio(String DK, AccountBank accountBank,
                              String remarks, PaymentDetails paymentDetails) {
        PortfolioAccount portfolioAccount = portoRepo.findPortfolioAccountByAccountBank(accountBank.getId());
        if (portfolioAccount == null) {
            portfolioAccount = PortfolioAccount.builder()
                    .accountBank(accountBank)
                    .amount(0d).build();
        }
        List<PortfolioAccountDet> portfolioAccountDet = portoDetRepo.findPortfolioAccountByAccountBank(accountBank.getId());
        Double sumAmount = portfolioAccountDet != null && portfolioAccountDet.size() > 0 ? portfolioAccountDet.stream().mapToDouble(PortfolioAccountDet::getAmount).sum() : paymentDetails.getPayment().getTotalAmount();
        portfolioAccount.setAmount(sumAmount);
        portfolioAccount = portoRepo.save(portfolioAccount);
        setPortfolioDet(DK, accountBank, remarks, paymentDetails, portfolioAccount);
    }

    private void setPortfolioDet(String DK, AccountBank accountBank,
                                 String remarks, PaymentDetails paymentDetails,
                                 PortfolioAccount portfolioAccount) {
        PortfolioAccountDet det = PortfolioAccountDet.builder()
                .portfolioAccount(portfolioAccount)
                .accountBank(accountBank)
                .transactionDate(paymentDetails.getCreatedAt())
                .dk(DK)
                .amount(DK != null && DK.equals("D") ? paymentDetails.getAmount() * -1 : paymentDetails.getAmount())
                .remarks(remarks).build();
        portoDetRepo.save(det);
    }
}
