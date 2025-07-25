package com.fadhlika.lokasi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.SignatureVerificationException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public void handleGenericException(RuntimeException ex) {
        logger.error("Exception handled: {}", ex.getMessage());
        throw ex;
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleMissingRequestCookieException(MissingRequestCookieException ex)
            throws MissingRequestCookieException {
        logger.error("MissingRequestCookieException: {}", ex.getMessage());
        throw ex;
    }

    @ExceptionHandler(SignatureVerificationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleSignatureVerificationException(SignatureVerificationException ex)
            throws SignatureVerificationException {
        logger.error("SignatureVerificationException: {}", ex.getMessage());
        throw ex;
    }
}
