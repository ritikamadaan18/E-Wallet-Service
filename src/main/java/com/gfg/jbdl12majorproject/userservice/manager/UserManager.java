package com.gfg.jbdl12majorproject.userservice.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfg.jbdl12majorproject.notification.NotificationRequest;
import com.gfg.jbdl12majorproject.notification.NotificationType;
import com.gfg.jbdl12majorproject.userservice.entities.PaymentUser;
import com.gfg.jbdl12majorproject.userservice.model.SignUpRequest;
import com.gfg.jbdl12majorproject.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class UserManager {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    ObjectMapper objectMapper = new ObjectMapper();

    public String create(SignUpRequest signUpRequest) throws JsonProcessingException {
        try {
            getUser(signUpRequest.getUsername());

            return "signup_form_again";

        }catch (UsernameNotFoundException exception){//if username not present then we make a new paymentUser
            PaymentUser user = PaymentUser.builder()
                    .username(signUpRequest.getUsername())
                    .password(bCryptPasswordEncoder.encode(signUpRequest.getPassword()))
                    .email(signUpRequest.getEmail())
                    .authority("user")
                    .authenticationProvider(signUpRequest.getAuthenticationProvider())
                    .build();
            userRepository.save(user);
            kafkaTemplate.send("createWallet",user.getUsername());//whenever we create a user we are also creating a wallet//we are saying create a wallet for this usernamw
                            //topic "createWallet" is in "WalletManagerImpl" class

            //to send notification that ur user has been created
            NotificationRequest notificationRequest = NotificationRequest
                    .builder()
                    .user(user.getUsername())
                    .type(NotificationType.USER_CREATED)
                    .message("Welcome ".concat(user.getUsername())).build();
            kafkaTemplate.send("notification",objectMapper.writeValueAsString(notificationRequest));
            return "register_success_local";
        }
    }

    public PaymentUser getUser(String id) {
        return userRepository.findByUsername(id).orElseThrow(()->new UsernameNotFoundException("username is not found for : "+id));
    }
}