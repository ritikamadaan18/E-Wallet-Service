package com.gfg.jbdl12majorproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {
    @Bean
    JavaMailSender javaMailSender(){//JavaMailSender->a client which is requesting google server to send emails(similar to kafkaTemplate or redisTemplate)
        //actually ur mail sender is google.so here u have to provide server details of google(smtp server)
        JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);//port we r going to use is there in google 587
        javaMailSender.setUsername("");
        javaMailSender.setPassword("");
        Properties properties=new Properties();
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.debug","true");
        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;

    }

    @Bean
    SimpleMailMessage simpleMailMessage(){//basically contains syntax for ur emailno(like to,from,cc,bcc etc.)
        return new SimpleMailMessage();
    }
}
