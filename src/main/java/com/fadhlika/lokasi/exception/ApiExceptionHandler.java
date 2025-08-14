package com.fadhlika.lokasi.exception;

import java.util.MissingResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.fadhlika.lokasi.dto.Response;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleException(Exception ex) {
        if (ex.getClass().isAnnotationPresent(ResponseStatus.class)) {
            ResponseStatus status = ex.getClass().getAnnotation(ResponseStatus.class);
            return new ResponseEntity<>(new Response<>(ex.getMessage()), status.value());
        }

        ex.printStackTrace();
        return new ResponseEntity<>(new Response<>(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<Response<Void>> handleMissingRequestCookieException(MissingRequestCookieException ex) {
        logger.error("MissingRequestCookieException: {}", ex.getMessage());
        return new ResponseEntity<>(new Response<>(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureVerificationException.class)
    public ResponseEntity<Response<Void>> handleSignatureVerificationException(SignatureVerificationException ex) {
        logger.error("SignatureVerificationException: {}", ex.getMessage());
        return new ResponseEntity<>(new Response<>(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingResourceException.class)
    public ResponseEntity<Response<Void>> handleMissingResourceException(MissingResourceException ex) {
        logger.error("MissingResourceException: {}", ex.getMessage());
        return new ResponseEntity<>(new Response<>(ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}
