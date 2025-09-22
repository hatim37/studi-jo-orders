package com.ecom.orders.response;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
       /* Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", new Date());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("error", "Error NotFound");
        errorBody.put("message", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);*/
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
