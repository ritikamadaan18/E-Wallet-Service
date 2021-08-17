package com.gfg.jbdl12majorproject.viewLayer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gfg.jbdl12majorproject.userservice.entities.AuthenticationProvider;
import com.gfg.jbdl12majorproject.userservice.manager.UserManager;
import com.gfg.jbdl12majorproject.userservice.model.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    @Autowired
    private UserManager userManager;

    @GetMapping("")
    public String  viewHomePage(){
        return "index";
    }
    @GetMapping("/register")
    public String showSignUpForm(){
        return "signup_form";
    }
    @GetMapping("/process_register")
    public String processRegsitration(@RequestParam("email") String user_email,@RequestParam("userid") String user_id,
    @RequestParam("password") String password) throws JsonProcessingException {
        SignUpRequest signUpRequest= SignUpRequest.builder()
                .username(user_id)
                .email(user_email)
                .authenticationProvider(AuthenticationProvider.LOCAL)
                .password(password)
                .build();
        String url=userManager.create(signUpRequest);
        return url;
    }

    @GetMapping("/login_success")
    public String getSuccessPage(){
        return "register_success_google";
    }

    @GetMapping("/login_again")
    public String getLoginPage(){
        return "signup_form_again";
    }

}
