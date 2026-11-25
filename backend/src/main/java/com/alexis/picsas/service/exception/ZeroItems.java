package com.alexis.picsas.service.exception;

public class ZeroItems extends RuntimeException {
    public ZeroItems(String message) {
        super(message);
    }
}
