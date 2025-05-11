package com.account_bank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PAYMENT")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "TRANSACTION_TYPE")
    private String transactionType;

    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;

    @Column(name = "STATUS_AUTHORIZATION")
    private String statusAuthorization;

    @Column(name = "STATUS_TRANSACTION")
    private String statusTransaction;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "payment")
    private List<PaymentDetails> details;

}
