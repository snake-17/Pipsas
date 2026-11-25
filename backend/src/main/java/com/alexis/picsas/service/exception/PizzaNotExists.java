package com.alexis.picsas.service.exception;

public class PizzaNotExists extends RuntimeException {
    public PizzaNotExists(String message) {
        super(message);
    }
}
