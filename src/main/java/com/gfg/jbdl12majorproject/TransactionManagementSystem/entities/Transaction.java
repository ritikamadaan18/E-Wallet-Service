package com.gfg.jbdl12majorproject.TransactionManagementSystem.entities;

import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionMethod;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionStatus;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionType;
import lombok.*;

import javax.persistence.*;

import java.util.Date;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String transactionId;
    @Column(nullable = false)
    private String fromUserId;
    @Column(nullable = false)
    private String  toUserId;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private String transactionType;
    @Column(nullable = false)
    private String transactionStatus;
    @Column(nullable = false)
    private String transactionMethod;
    @Temporal(value = TemporalType.TIMESTAMP)//temporal is for converting java date time etc into compatible database type
    private Date createdAt;
}
