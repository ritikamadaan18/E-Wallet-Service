package com.gfg.jbdl12majorproject.TransactionManagementSystem.entities;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority implements GrantedAuthority {
    private String authority;
    @Override
    public String getAuthority() {
        return this.authority;
    }
}