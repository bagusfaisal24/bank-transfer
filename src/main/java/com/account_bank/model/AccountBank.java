package com.account_bank.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ACCOUNT_BANK")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ACCOUNT_HOLDER", nullable = false)
    private String account_holder;

    @Column(name = "ACCOUNT_NUMBER", nullable = false)
    private String account_number;
}
