package com.account_bank.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "LOG_HIST_PAYMENT")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogHistTrans extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "STATUS_AUTHORIZATION")
    private String statusAuthorization;

    @Column(name = "STATUS_TRANSACTION")
    private String statusTransaction;
}
