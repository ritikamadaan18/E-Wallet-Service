package com.gfg.jbdl12majorproject.TransactionManagementSystem.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfg.jbdl12majorproject.cache.RedisEntity;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.entities.Transaction;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionRequest;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionResponse;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionStatus;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionUpdate;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.repository.TransactionRepository;
import com.gfg.jbdl12majorproject.notification.NotificationRequest;
import com.gfg.jbdl12majorproject.notification.NotificationType;
import com.gfg.jbdl12majorproject.wallet.model.UpdateWalletRequest;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;


@Service
public class TransactionManagerImpl implements TransactionManager {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest, UsernamePasswordAuthenticationToken loggedInUser) throws JsonProcessingException {

        if (loggedInUser.getName().equals(transactionRequest.getFromUserId())) {

            Transaction transaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .transactionMethod(transactionRequest.getTransactionMethod().toString())
                    .transactionStatus(TransactionStatus.PENDING.toString())
                    .amount(transactionRequest.getAmount())
                    .transactionType(transactionRequest.getTransactionType().toString())
                    .currency(transactionRequest.getCurrency())
                    .toUserId(transactionRequest.getToUserId())
                    .fromUserId(transactionRequest.getFromUserId())
                    .createdAt(new Date())//creating new date
                    .build();
            Transaction transactionSaved = transactionRepository.save(transaction);

            //whenever transaction is created we are also updating wallet
            UpdateWalletRequest updateWalletRequest = UpdateWalletRequest.builder()
                    .amount(transaction.getAmount())
                    .fromUser(transaction.getFromUserId())
                    .toUser(transaction.getToUserId())
                    .transactionId(transaction.getTransactionId())
                    .transactionType(transaction.getTransactionType().toString())
                    .build();

            kafkaTemplate.send("updateWallet", objectMapper.writeValueAsString(updateWalletRequest));
            //topic updateWallet is in "WalletManagerImpl" class
            //the orchestrator is sending this request as an event,,,,,,similar to using REST call for userservice

            return TransactionResponse.builder()
                    .transactionId(transactionSaved.getTransactionId())
                    .transactionStatus(transactionSaved.getTransactionStatus().toString())
                    .build();
        } else {
            throw new UsernameNotFoundException("username not correct");
        }

    }

    @Override
    public TransactionResponse getTransaction(String id) throws NotFoundException {
        Transaction transaction = transactionRepository.findByTransactionId(id)
                .orElseThrow(() -> new NotFoundException("transaction is not present"));
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .transactionStatus(transaction.getTransactionStatus())
                .build();
    }

    @Override
    @KafkaListener(topics = "transactionUpdate", groupId = "transaction")
    public void updateTransaction(String updateRequest) throws JsonProcessingException {
        TransactionUpdate transactionUpdate = objectMapper.readValue(updateRequest, TransactionUpdate.class);
        try {
            Transaction transaction = transactionRepository.findByTransactionId(transactionUpdate.getTransactionId())
                    .orElseThrow(() -> new NotFoundException("transaction is not present"));
            transaction.setTransactionStatus(transactionUpdate.getTransactionStatus().toString());
            RedisEntity entity=RedisEntity.builder()
                    .id(transaction.getFromUserId())
                    .transactionId(transaction.getTransactionId())
                    .toUserId(transaction.getToUserId())
                    .amount(transaction.getAmount())
                    .currency(transaction.getCurrency())
                    .transactionType(transaction.getTransactionType())
                    .transactionStatus(transaction.getTransactionStatus())
                    .transactionMethod(transaction.getTransactionMethod())
                    .createdAt(transaction.getCreatedAt())
                    .build();

            kafkaTemplate.send("lastTransaction",objectMapper.writeValueAsString(entity));
            transactionRepository.save(transaction);

            NotificationRequest notificationRequest = null;
            //to give notification to ur user about status of the transaction
            //this notification is for "fromUser"
            if (transaction.getTransactionStatus().equals("SUCCESS")) {
                if (transaction.getTransactionType().equals("CREDIT")) {
                    notificationRequest = NotificationRequest.builder()
                            .user(transaction.getFromUserId())
                            .type(NotificationType.TRANSACTION_UPDATED)
                            .message("Trasaction with id ".concat(transaction.getTransactionId()).concat(" ")
                                    .concat(transaction.getTransactionStatus()) + "\n" + "INR ".concat(String.valueOf(transaction.getAmount()))
                                    .concat(" transferred from your Wallet to ").concat(transaction.getToUserId()))
                            .build();}
//                else if (transaction.getTransactionType().equals("DEBIT")) {
//                    notificationRequest = NotificationRequest.builder()
//                            .user(transaction.getFromUserId())
//                            .type(NotificationType.TRANSACTION_UPDATED)
//                            .message("Trasaction with id ".concat(transaction.getTransactionId()).concat(" ")
//                                    .concat(transaction.getTransactionStatus()) + "\n" + "INR ".concat(String.valueOf(transaction.getAmount()))
//                                    .concat(" transferred to your Wallet from ").concat(transaction.getToUserId()))
//                            .build();
//                }
                kafkaTemplate.send("notification", objectMapper.writeValueAsString(notificationRequest));
            } else if (transaction.getTransactionStatus().equals("REJECTED")) {
                notificationRequest = NotificationRequest.builder()
                        .user(transaction.getFromUserId())
                        .type(NotificationType.TRANSACTION_UPDATED)
                        .message("Trasaction with id ".concat(transaction.getTransactionId()).concat(" ")
                                .concat(transaction.getTransactionStatus()))
                        .build();
                kafkaTemplate.send("notification", objectMapper.writeValueAsString(notificationRequest));
            }


            //this notification is for "toUser"
            notificationRequest.setUser(transaction.getToUserId());
            if (transaction.getTransactionStatus().equals("SUCCESS")) {
                notificationRequest.setMessage("Your wallet got ".concat(transaction.getTransactionType()).concat("ED INR ")
                        .concat(String.valueOf(transaction.getAmount())).concat(" from ").concat(transaction.getFromUserId()));
                kafkaTemplate.send("notification", objectMapper.writeValueAsString(notificationRequest));
            }

        } catch (NotFoundException e) {
            e.printStackTrace();
        }

    }
}
