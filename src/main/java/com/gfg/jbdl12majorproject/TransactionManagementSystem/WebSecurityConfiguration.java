package com.gfg.jbdl12majorproject.TransactionManagementSystem;


import com.gfg.jbdl12majorproject.TransactionManagementSystem.manager.UserDetailsServiceImpl;
import com.gfg.jbdl12majorproject.oauth.CustumOAuth2User;
import com.gfg.jbdl12majorproject.oauth.CustumOAuth2UserService;
import com.gfg.jbdl12majorproject.userservice.entities.AuthenticationProvider;
import com.gfg.jbdl12majorproject.userservice.entities.PaymentUser;
import com.gfg.jbdl12majorproject.userservice.manager.UserManager;
import com.gfg.jbdl12majorproject.userservice.model.SignUpRequest;
import com.gfg.jbdl12majorproject.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userManager;

    @Bean
    PasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    //Authentication
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userManager)
                .passwordEncoder(bCryptPasswordEncoder())
                ;
    }
    @Autowired
    private CustumOAuth2UserService oAuthUserService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserManager usrManager;

    //Authorization
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/oauth2/**").permitAll()
                .antMatchers("/transaction/**")
                .authenticated()
                .antMatchers("/addAmount/**")
                .authenticated()
                .antMatchers("/signUp/**")
                .permitAll()
                .antMatchers("/user/**")
                .permitAll()
                .and()
                .oauth2Login()
                .loginPage("/signup_form")
                .userInfoEndpoint()
                .userService(oAuthUserService)
                .and()
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException, ServletException {


                        System.out.println(authentication.getPrincipal().toString());

                        CustumOAuth2User oauthUser=new CustumOAuth2User((OAuth2User) authentication.getPrincipal());

                        try{
                            getUser(oauthUser.getName());
                            response.sendRedirect("/login_again");
                        }catch(UsernameNotFoundException e){
                            SignUpRequest signUpRequest=SignUpRequest.builder()
                                    .username(oauthUser.getName())
                                    .email(oauthUser.getEmail())
                                    .authenticationProvider(AuthenticationProvider.GOOGLE)
                                    .password("")
                                    .build();
                            usrManager.create(signUpRequest);
                            //to send notification that ur user has been created
                            response.sendRedirect("/login_success");
                        }

                    }
                })
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable();
    }
    public PaymentUser getUser(String id) {
        return userRepository.findByUsername(id).orElseThrow(()->new UsernameNotFoundException("username is not found for : "+id));
    }


}