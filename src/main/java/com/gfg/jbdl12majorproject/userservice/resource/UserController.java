package com.gfg.jbdl12majorproject.userservice.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfg.jbdl12majorproject.notification.NotificationRequest;
import com.gfg.jbdl12majorproject.notification.NotificationType;
import com.gfg.jbdl12majorproject.oauth.CustumOAuth2User;
import com.gfg.jbdl12majorproject.userservice.entities.PaymentUser;
import com.gfg.jbdl12majorproject.userservice.manager.UserManager;
import com.gfg.jbdl12majorproject.userservice.model.SignUpRequest;
import com.gfg.jbdl12majorproject.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserManager userManager;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signUp")
    public void signUp(@RequestBody SignUpRequest signupRequest) throws JsonProcessingException {
        userManager.create(signupRequest);
    }

    @GetMapping("/user/{id}")
    public PaymentUser getUser(@PathVariable("id") String id){
        PaymentUser user  = userManager.getUser(id);
        return user;
    }
    @PostMapping("/process_password")
    public String AddPswrd(@RequestParam("password") String pswrd, Authentication authentication) throws JsonProcessingException {
        CustumOAuth2User oauthUser=new CustumOAuth2User((OAuth2User) authentication.getPrincipal());
        PaymentUser user  = userManager.getUser(oauthUser.getName());
        user.setPassword(bCryptPasswordEncoder.encode(pswrd));
        userRepository.save(user);
        System.out.println(oauthUser.getName());
        NotificationRequest notificationRequest = NotificationRequest
                .builder()
                .user(user.getUsername())
                .type(NotificationType.PASSWORD_CREATED)
                .message("Your password changed successfully.").build();
        kafkaTemplate.send("notification",objectMapper.writeValueAsString(notificationRequest));
        return "Welcome to our application";
    }

}