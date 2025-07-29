package com.fadhlika.lokasi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.fadhlika.lokasi.dto.Response;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> handleGenericException(RuntimeException ex) {
        logger.error("Exception handled: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body(new Response(ex.getMessage()));
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<Response> handleMissingRequestCookieException(MissingRequestCookieException ex)
            throws MissingRequestCookieException {
        logger.error("MissingRequestCookieException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(ex.getMessage()));
    }

    @ExceptionHandler(SignatureVerificationException.class)
    public ResponseEntity<Response> handleSignatureVerificationException(SignatureVerificationException ex)
            throws SignatureVerificationException {
        logger.error("SignatureVerificationException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(ex.getMessage()));
    }
}
