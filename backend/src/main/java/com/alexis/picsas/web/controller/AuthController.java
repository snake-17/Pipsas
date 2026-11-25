package com.alexis.picsas.web.controller;

import com.alexis.picsas.service.AuthService;
import com.alexis.picsas.service.dto.LoginDto;
import com.alexis.picsas.service.dto.RegisterDto;
import com.alexis.picsas.web.config.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginDto loginDto){
        String jwt = authService.login(loginDto);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + jwt)
                .build();
    }
    @PostMapping("/register")
    public ResponseEntity<Void> registerController(@RequestBody RegisterDto registerDto){
        authService.registerService(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }
}
