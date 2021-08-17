package com.gfg.jbdl12majorproject.TransactionManagementSystem.resources;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.manager.TransactionManager;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionRequest;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionResponse;
import com.gfg.jbdl12majorproject.cache.RedisEntity;
import com.gfg.jbdl12majorproject.cache.RedisRepository;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@Slf4j
public class Controller {
    @Autowired
    private TransactionManager transactionManager;

    @Autowired
    private RedisRepository redisRepository;

    @PostMapping("/transaction")
    public ResponseEntity createTransaction(@RequestBody TransactionRequest transactionRequest, Authentication authentication){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(),authentication.getAuthorities());
        TransactionResponse transactionResponse= null;
        try {
            transactionResponse = transactionManager.createTransaction(transactionRequest,usernamePasswordAuthenticationToken);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.accepted().body(transactionResponse);
    }
    @GetMapping("/get_last_transaction/{user_id}")
    public RedisEntity getLastTransaction(@PathVariable("user_id") String user_id){
        RedisEntity redisEntity=redisRepository.findById(user_id)
                .orElseThrow(()->new NoSuchElementException("user "+user_id+" does not exists"));
        return redisEntity;
    }

    @GetMapping("/transaction/{id}")
    public ResponseEntity getTransaction(@PathVariable("id") String id){
        TransactionResponse transactionResponse= null;
        try {
            transactionResponse = transactionManager.getTransaction(id);
            return ResponseEntity.ok(transactionResponse);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().build();
        }


    }

}
