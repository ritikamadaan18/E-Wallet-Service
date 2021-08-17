package com.gfg.jbdl12majorproject.TransactionManagementSystem.manager;

import com.gfg.jbdl12majorproject.TransactionManagementSystem.entities.Authority;
import com.gfg.jbdl12majorproject.userservice.entities.PaymentUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.gfg.jbdl12majorproject.TransactionManagementSystem.entities.User;

import java.util.Arrays;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    //whenever UserDetailsService will need some userDetails,we will call our userService to get PaymentUser
    //and with this Payment user details we are building the user details which is actually login user details
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        RestTemplate restTemplate = new RestTemplate();//RestTemplate is to call other service(microservice)(for e.g. to call API of other service)
        PaymentUser user = restTemplate.getForEntity("http://localhost:8080/user/".concat(username), PaymentUser.class).getBody();
        //"http://localhost:8080/user/".concat(username)----->This is an API in UserController class in userservice package
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorityList(Arrays.asList(new Authority(user.getAuthority())))
                .build();
    }
}
