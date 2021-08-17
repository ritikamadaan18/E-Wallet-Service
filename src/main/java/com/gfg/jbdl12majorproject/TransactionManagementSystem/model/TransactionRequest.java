package com.gfg.jbdl12majorproject.TransactionManagementSystem.model;

import lombok.*;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransactionRequest {
    private String fromUserId;//id of user from which money is to be sent

    private String  toUserId;//id of user to which the money is to be sent

    private Double amount;

    private String currency;

    private TransactionType transactionType;

    private TransactionMethod transactionMethod;
}
