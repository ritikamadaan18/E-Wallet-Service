package com.gfg.jbdl12majorproject.wallet.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfg.jbdl12majorproject.ForbiddenException;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionStatus;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionType;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.model.TransactionUpdate;
import com.gfg.jbdl12majorproject.notification.NotificationRequest;
import com.gfg.jbdl12majorproject.notification.NotificationType;
import com.gfg.jbdl12majorproject.wallet.entity.Wallet;
import com.gfg.jbdl12majorproject.wallet.model.UpdateWalletRequest;
import com.gfg.jbdl12majorproject.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class WalletManagerImpl implements WalletManager{
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;//to produce an event to ur transaction management system to tell if your transaction is success or failure

    //in function updateWallet->u are getting a string,u need to convert it to java object,for that u need objectMapper
    //it converts json request to java object
    ObjectMapper objectMapper=new ObjectMapper();

    @Override
    @KafkaListener(topics = "createWallet", groupId = "wallet")//this is also kafkaListener,when creating user u will also create wallet for the user
    public void createWallet(String user) {
        Wallet wallet=Wallet.builder()
                .balance(0D)
                .userId(user)
                .build();
        walletRepository.save(wallet);

    }

    @Override
    @KafkaListener(topics = "updateWallet", groupId = "wallet")
    public void updateWallet(String updateRequest) throws JsonProcessingException {
        UpdateWalletRequest updateWalletRequestObj =objectMapper.readValue(updateRequest, UpdateWalletRequest.class);//coverts string updateRequest to object of class UpdateRequest(java object)

        try {
            Wallet fromUserWallet = walletRepository.findByUserId(updateWalletRequestObj.getFromUser())
                    .orElseThrow(() -> new Exception("wallet is not found for user"));
            Wallet toUserWallet = walletRepository.findByUserId(updateWalletRequestObj.getToUser())
                    .orElseThrow(() -> new Exception("wallet is not found for user"));
            if (updateWalletRequestObj.getTransactionType().equalsIgnoreCase(TransactionType.CREDIT.name())) {
                if (fromUserWallet.getBalance() - updateWalletRequestObj.getAmount() < 0) {
                    throw new Exception("not sufficient balance");
                }
                fromUserWallet.setBalance(fromUserWallet.getBalance() - updateWalletRequestObj.getAmount());
                toUserWallet.setBalance(toUserWallet.getBalance() + updateWalletRequestObj.getAmount());
            }

            if (updateWalletRequestObj.getTransactionType().equalsIgnoreCase(TransactionType.DEBIT.name())) {
                if (toUserWallet.getBalance() - updateWalletRequestObj.getAmount() < 0) {
                    throw new Exception("not sufficient balance");
                }
                toUserWallet.setBalance(toUserWallet.getBalance() - updateWalletRequestObj.getAmount());
                fromUserWallet.setBalance(fromUserWallet.getBalance() + updateWalletRequestObj.getAmount());
            }
            walletRepository.save(fromUserWallet);
            walletRepository.save(toUserWallet);//if transaction is successful,it will update wallet
                //and then send success msg
            TransactionUpdate transactionUpdate = TransactionUpdate
                    .builder()
                    .transactionId(updateWalletRequestObj.getTransactionId())
                    .transactionStatus(TransactionStatus.SUCCESS)
                    .build();
            kafkaTemplate.send("transactionUpdate", objectMapper.writeValueAsString(transactionUpdate));
                            //topic "transactionUpdate" is in TransactionManagerImpl class
        }catch(Exception e){
            TransactionUpdate transactionUpdate = TransactionUpdate
                    .builder()
                    .transactionId(updateWalletRequestObj.getTransactionId())
                    .transactionStatus(TransactionStatus.REJECTED)
                    .build();
            kafkaTemplate.send("transactionUpdate", objectMapper.writeValueAsString(transactionUpdate));
            //here topic "transactionUpdate",it is present in TransactionManagerImpl class

        }
    }

    @Override
    public void addAmount(String userID,Double amount, UsernamePasswordAuthenticationToken loggedInUser) throws ForbiddenException, JsonProcessingException {


        if(loggedInUser.getName().equals(userID)){
            Wallet wallet=walletRepository.findByUserId(userID)
                    .orElseThrow(() -> new ForbiddenException("wallet is not found for user"));
            wallet.setBalance(wallet.getBalance() +amount);
            walletRepository.save(wallet);

            NotificationRequest notificationRequest= NotificationRequest.builder()
                    .user(userID)
                    .type(NotificationType.AMOUNT_ADDED)
                    .message("AMOUNT:".concat(amount.toString()).concat(" got added to your wallet"))
                    .build();
            kafkaTemplate.send("notification",objectMapper.writeValueAsString(notificationRequest));
        }else{
            throw new ForbiddenException("Incorrect credentials provided...");
        }
    }
}
