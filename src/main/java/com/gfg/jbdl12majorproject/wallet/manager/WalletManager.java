package com.gfg.jbdl12majorproject.wallet.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gfg.jbdl12majorproject.ForbiddenException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface WalletManager {
    public void createWallet(String user);
    public void updateWallet(String updateRequest) throws JsonProcessingException;//taking "updateRequest" as string because updateWallet request will come
                                //from kafka .kafka event will be taking as a string and later we convert "updateRequest" string to
                                //an UpdateRequest object.
    public void addAmount(String userID,Double amount, UsernamePasswordAuthenticationToken loggedInUser) throws Exception;
}
