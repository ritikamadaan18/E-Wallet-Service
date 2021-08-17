package com.gfg.jbdl12majorproject.userservice.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUser {//this is just user object and not part of authenticated system
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String authority="user";//in payment application there can be user or admin so here by default we set it to user
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthenticationProvider authenticationProvider;
}