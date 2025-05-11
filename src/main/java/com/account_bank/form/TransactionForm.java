package com.account_bank.form;

import com.account_bank.cons.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionForm {
    private String sourceAccount;
    private String destinationAccount;
    private TransactionType transactionType;
    private Double amount;
}
