package com.example.springpipeline.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.springpipeline.security.JwtService;

@RestController
@RequestMapping("/api/auth")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class AuthController {

    private final String username;
    private final String password;
    private final JwtService jwtService;

    public AuthController(
            @Value("${security.auth.username:admin}") String username,
            @Value("${security.auth.password:changeit}") String password,
            JwtService jwtService
    ) {
        this.username = (username == null || username.isBlank()) ? "admin" : username;
        this.password = (password == null || password.isBlank()) ? "changeit" : password;
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public AuthResponse token(@RequestBody AuthRequest request) {
        // Validate login payload against configured credentials.
        if (!username.equals(request.username()) || !password.equals(request.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Issue a signed JWT used as Bearer token for protected endpoints.
        String token = jwtService.generateToken(username);
        return new AuthResponse(token);
    }
}


