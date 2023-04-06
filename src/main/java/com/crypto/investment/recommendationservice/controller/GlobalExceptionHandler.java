package com.crypto.investment.recommendationservice.controller;

import com.crypto.investment.recommendationservice.repository.CSVDaoReadException;
import com.crypto.investment.recommendationservice.service.CryptoNotSupportedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CryptoNotSupportedException.class)
    public ResponseEntity<Object> handleCryptoNotSupportedException(CryptoNotSupportedException ex) {
        return generateErrorResponse(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(CSVDaoReadException.class)
    public ResponseEntity<Object> handleCSVDaoReadException(CSVDaoReadException ex) {
        return generateErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> generateErrorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(Map.of("message", message), new HttpHeaders(), status);
    }
}
