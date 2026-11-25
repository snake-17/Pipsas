package com.alexis.picsas.service.exception;

public class OrderIdNotExists extends RuntimeException {
    public OrderIdNotExists(String message) {
        super(message);
    }
}
