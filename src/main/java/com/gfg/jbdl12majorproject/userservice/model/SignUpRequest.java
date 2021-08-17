package com.gfg.jbdl12majorproject.userservice.model;

import com.gfg.jbdl12majorproject.userservice.entities.AuthenticationProvider;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SignUpRequest {
    private String username;
    private String password;

    @Override
    public String toString() {
        return "SignUpRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
    private AuthenticationProvider authenticationProvider;
    private String email;
}
