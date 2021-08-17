package com.gfg.jbdl12majorproject.TransactionManagementSystem.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransactionUpdate {//linked with wallet
    //if transaction is successful as per walletManager,it tells that for this transactionId ur transaction is successful
    private String transactionId;
    private TransactionStatus transactionStatus;
}
