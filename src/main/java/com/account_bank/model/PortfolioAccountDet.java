package com.account_bank.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PORTFOLIO_DET")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioAccountDet extends BaseModel{

    public static final String DEBET = "D";
    public static final String KREDIT = "K";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private AccountBank accountBank;

    @ManyToOne
    @JoinColumn(name = "PORTO_ID")
    private PortfolioAccount portfolioAccount;

    @Column(name = "TRANSACTION_DATE")
    private Timestamp transactionDate;

    private String dk;

    private Double amount;

    private String remarks;

}
