package com.alexis.picsas.service.exception;

public class CustomerNotExists extends RuntimeException {
    public CustomerNotExists(String message) {
        super("El cliente con nombre "+message+ " no existe.");
    }
}
