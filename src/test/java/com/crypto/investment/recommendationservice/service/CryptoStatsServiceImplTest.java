package com.crypto.investment.recommendationservice.service;

import com.crypto.investment.recommendationservice.models.CryptoStat;
import com.crypto.investment.recommendationservice.models.CryptoStatAggregated;
import com.crypto.investment.recommendationservice.repository.CryptoStatsCSVDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CryptoStatsServiceImplTest {
    private static final LocalDateTime DATE = LocalDate.now(ZoneOffset.UTC).minusMonths(2).atStartOfDay();
    private static final String BTC = "BTC";
    private static final String ETH = "ETH";
    private static final String DOGE = "DOGE";
    private static final Map<String, List<CryptoStat>> resource = Map.of(
            BTC, generateCryptoStats(BTC, 4.23, 1.3, 3.26, 3.11),
            ETH, generateCryptoStats(ETH, 2.87, 0.95, 1.32, 1.56),
            DOGE, generateCryptoStats(DOGE, 5.3, 1.1, 2.6, 3.8)
    );

    @Mock
    private CryptoStatsCSVDao dao;
    @Mock
    private CryptoValidator validator;
    @InjectMocks
    private CryptoStatsServiceImpl service = new CryptoStatsServiceImpl();

    @Before
    public void setUp() {
        when(dao.getAllCryptoStats()).thenReturn(resource);
        when(dao.getAllCryptoStatsWithMonthRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(resource);
        when(dao.getStatsForCrypto(anyString())).thenAnswer(s ->{
            var param = s.getArgument(0 ,String.class);
            return resource.getOrDefault(param, Collections.emptyList());
        });
        when(dao.getStatsForCryptoWithMonthRange(anyString(), any(LocalDate.class), any(LocalDate.class))).thenAnswer(s ->{
            var param = s.getArgument(0 ,String.class);
            return resource.getOrDefault(param, Collections.emptyList());
        });
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(validator, dao);
    }

    @Test
    public void testReturnByCryptoSuccess() {
        Optional<CryptoStatAggregated> result = service.getAggregatedStatsForCrypto(BTC, null);
        assertTrue(result.isPresent());
        assertEquals(result.get().getMaxPrice().doubleValue(), 4.23, 0.001);
        assertEquals(result.get().getMinPrice().doubleValue(), 1.3, 0.001);
        assertEquals(result.get().getOldestPrice().doubleValue(), 3.26, 0.001);
        assertEquals(result.get().getNewestPrice().doubleValue(), 3.11, 0.001);
        verify(validator, times(1)).validateSupportedCrypto(BTC);
        verify(dao, times(1)).getStatsForCrypto(BTC);
    }

    @Test
    public void testReturnByCryptoWithSpecifiedMonths() {
        assertTrue(service.getAggregatedStatsForCrypto(ETH, 2).isPresent());
        verify(validator, times(1)).validateSupportedCrypto(ETH);
        verify(dao, times(1)).getStatsForCryptoWithMonthRange(eq(ETH), eq(DATE.toLocalDate()), eq(LocalDate.now(ZoneOffset.UTC).plusMonths(1)));
    }

    @Test
    public void shouldReturnResultInDescendingOrderByNormalizedRange() {
        SortedSet<CryptoStatAggregated> result = service.getAggregatedStatsPerCryptoInDescendingOrder(null);
        assertEquals(result.first().getSymbol(), ETH);
        assertEquals(result.last().getSymbol(), DOGE);
        verify(dao, times(1)).getAllCryptoStats();
    }

    @Test
    public void shouldReturnResultSetWithSpecifiedMonths() {
        assertFalse(service.getAggregatedStatsPerCryptoInDescendingOrder(2).isEmpty());
        verify(dao, times(1)).getAllCryptoStatsWithMonthRange(eq(DATE.toLocalDate()), eq(LocalDate.now(ZoneOffset.UTC).plusMonths(1)));
    }

    @Test
    public void shouldHighestByNormalizedRangeForDay() {
        Optional<CryptoStatAggregated> result = service.getCryptoWithHighestNormalizedRangeForDay(DATE.toLocalDate());
        assertTrue(result.isPresent());
        assertEquals(result.get().getSymbol(), DOGE);
        verify(dao, times(1)).getAllCryptoStatsWithMonthRange(eq(DATE.toLocalDate()), eq(DATE.plusMonths(1).toLocalDate()));
    }

    @Test
    public void shouldEmptyResultForDayWithNoData() {
        var emptyDay = DATE.plusDays(1);
        Optional<CryptoStatAggregated> result = service.getCryptoWithHighestNormalizedRangeForDay(emptyDay.toLocalDate());
        assertFalse(result.isPresent());
        verify(dao, times(1)).getAllCryptoStatsWithMonthRange(eq(emptyDay.toLocalDate()), eq(emptyDay.plusMonths(1).toLocalDate()));
    }

    private static List<CryptoStat> generateCryptoStats(String symbol, double max, double min, double oldest, double newest) {
        List<CryptoStat> cryptoStats = new ArrayList<>();
        cryptoStats.add(new CryptoStat(DATE, symbol, new BigDecimal(oldest)));
        cryptoStats.add(new CryptoStat(DATE.plusHours(1), symbol, new BigDecimal(max)));
        cryptoStats.add(new CryptoStat(DATE.plusHours(2), symbol, new BigDecimal(min)));
        cryptoStats.add(new CryptoStat(DATE.plusHours(3), symbol, new BigDecimal(newest)));
        return cryptoStats;
    }

}