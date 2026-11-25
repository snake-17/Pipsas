package com.alexis.picsas.service.exception;

public class UserNotExist extends RuntimeException {
    public UserNotExist(String message) {
        super("El usuario "+message+". No fue encontrado.");
    }
}
