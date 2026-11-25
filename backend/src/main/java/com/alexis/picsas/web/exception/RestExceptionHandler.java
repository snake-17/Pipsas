package com.alexis.picsas.web.exception;

import com.alexis.picsas.service.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(UserAlreadyExists.class)
    ResponseEntity<Error> handleException(UserAlreadyExists ex){
        Error error = new Error("User already exists. ", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(EmailApiException.class)
    ResponseEntity<Error> handleException(EmailApiException ex){
        Error error = new Error("Email-error. ", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(CustomerNotExists.class)
    ResponseEntity<Error> handleException(CustomerNotExists ex){
        Error error = new Error("Customer not exists. ", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(OrderIdNotExists.class)
    ResponseEntity<Error> handleException(OrderIdNotExists ex){
        Error error = new Error("Order not exists. ", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(PizzaNotExists.class)
    ResponseEntity<Error> handleException(PizzaNotExists ex){
        Error error = new Error("Pizza not exists. ", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(UserNotExist.class)
    ResponseEntity<Error> handleException(UserNotExist ex){
        Error error = new Error("User not exists. ", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(ZeroItems.class)
    ResponseEntity<Error> handleException(ZeroItems ex){
        Error error = new Error("You have zero items on your cart. ", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
