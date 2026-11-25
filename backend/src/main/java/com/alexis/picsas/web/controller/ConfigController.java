package com.alexis.picsas.web.controller;

import com.alexis.picsas.service.ConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }
    @PostMapping("/toEmployee/{username}")
    public ResponseEntity<Void> becomeToEmployee(@PathVariable String username){
        configService.toEmployeeService(username);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
