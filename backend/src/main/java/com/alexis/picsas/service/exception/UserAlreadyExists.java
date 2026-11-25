package com.alexis.picsas.service.exception;

public class UserAlreadyExists extends RuntimeException{
    public UserAlreadyExists(String username) {
        super("El usuario "+username+" ya esta registrado");
    }
}
