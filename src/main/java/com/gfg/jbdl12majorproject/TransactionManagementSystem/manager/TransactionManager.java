package com.gfg.jbdl12majorproject.TransactionManagementSystem.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionRequest;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionResponse;
import javassist.NotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface TransactionManager {
    TransactionResponse createTransaction(TransactionRequest transactionRequest, UsernamePasswordAuthenticationToken managerId) throws JsonProcessingException;

    TransactionResponse getTransaction(String id) throws NotFoundException;

    void updateTransaction(String updateRequest) throws JsonProcessingException;//related to wallet
}
