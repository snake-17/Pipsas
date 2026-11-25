package com.alexis.picsas.web.exception;

public record Error(
        String type,
        String message
) {
}
