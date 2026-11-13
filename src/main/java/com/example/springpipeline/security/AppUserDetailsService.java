package com.example.springpipeline.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserDetails userDetails;

    public AppUserDetailsService(
            @Value("${security.auth.username:admin}") String username,
            @Value("${security.auth.password:changeit}") String password,
            PasswordEncoder passwordEncoder
    ) {
        String resolvedUsername = (username == null || username.isBlank()) ? "admin" : username;
        String resolvedPassword = (password == null || password.isBlank()) ? "changeit" : password;

        this.userDetails = User.withUsername(resolvedUsername)
                .password(passwordEncoder.encode(resolvedPassword))
                .roles("USER")
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!userDetails.getUsername().equals(username)) {
            throw new UsernameNotFoundException("User not found");
        }
        return userDetails;
    }
}


