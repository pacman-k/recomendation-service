package com.crypto.investment.recommendationservice.repository;

import com.crypto.investment.recommendationservice.models.CryptoStat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CryptoStatsCSVDaoTest {

    private static final String BTC = "BTC";
    private static final String ETH = "ETH";

    @Mock
    private CSVDaoConfig config;
    @InjectMocks
    private CryptoStatsCSVDao dao;

    @Before
    public void setUp() {
        when(config.getDatasourcePath()).thenReturn(Paths.get("src", "test", "resources"));
        when(config.getFilePattern()).thenReturn("$SYMBOL_$DATE_values.csv");
        when(config.getSupportedCryptos()).thenReturn(Set.of(BTC, ETH));
    }

    @Test
    public void shouldReturnStatsForSpecifiedCrypto() {
        var result = dao.getStatsForCrypto(BTC);
        assertEquals(result.size(), 3);
        result.stream().map(CryptoStat::getSymbol).forEach(s -> assertEquals(s, BTC));
    }

    @Test
    public void shouldReturnEmptyForIfNotFound() {
        assertEquals(dao.getStatsForCrypto("RAND").size(), 0);
    }

    @Test
    public void shouldReturnAllCryptoStats() {
        var result = dao.getAllCryptoStats();
        assertTrue(result.containsKey(BTC) && result.containsKey(ETH));
        assertEquals(3, result.get(BTC).size());
        assertEquals(3, result.get(ETH).size());
    }
}