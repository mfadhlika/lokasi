package com.fadhlika.lokasi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public void handleGenericException(RuntimeException ex) {
        logger.error("Exception handled: {}", ex.getMessage());
        throw ex;
    }
}
