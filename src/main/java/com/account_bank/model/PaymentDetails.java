package com.account_bank.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PAYMENT_DETAILS")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "PAYMENT_ID")
    @ManyToOne
    private Payment payment;

    @Column(name = "SOURCE_ACCOUNT")
    private String sourceAccount;

    @Column(name = "DESTINATION_ACCOUNT")
    private String destinationAccount;

    private Double amount;
}
