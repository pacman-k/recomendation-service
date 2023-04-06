package com.crypto.investment.recommendationservice.service;

import com.crypto.investment.recommendationservice.repository.DaoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
public class CryptoValidator {

    @Autowired
    private DaoConfig daoConfig;

    public void validateSupportedCrypto(String symbol) {
        if (symbol == null || !daoConfig.getSupportedCryptos().contains(symbol.toUpperCase())) {
            throw new CryptoNotSupportedException("Specified crypto is not supported: " + symbol);
        }
    }
}
