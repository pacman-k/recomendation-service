package com.crypto.investment.recommendationservice.service;

public class CryptoNotSupportedException extends RuntimeException{

    public CryptoNotSupportedException() {
    }

    public CryptoNotSupportedException(String message) {
        super(message);
    }
}
