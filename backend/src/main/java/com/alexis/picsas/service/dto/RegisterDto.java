package com.alexis.picsas.service.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String address;
}
