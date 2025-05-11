package com.account_bank.form;

import lombok.Data;

import java.util.List;

@Data
public class BulkForm {

    private String sourceAccount;
    private List<String> destinationAccount;
}
