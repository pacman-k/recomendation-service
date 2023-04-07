package com.crypto.investment.recommendationservice.service;

import com.crypto.investment.recommendationservice.repository.DaoConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CryptoValidatorTest {
    private static final String BTS = "BTS";

    @Mock
    private DaoConfig config;
    @InjectMocks
    private CryptoValidator validator;

    @Before
    public void setUp() {
        when(config.getSupportedCryptos()).thenReturn(Set.of(BTS));
    }

    @Test
    public void shouldThrowExceptionForUnsupportedCrypto() {
        assertThrows(CryptoNotSupportedException.class, () -> validator.validateSupportedCrypto("RAND"));
    }

    @Test
    public void shouldNotThrowExceptionForSupportedCrypto() {
        validator.validateSupportedCrypto(BTS);
    }
}