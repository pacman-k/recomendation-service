package com.crypto.investment.recommendationservice.repository;

import com.crypto.investment.recommendationservice.models.CryptoStat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Dao interface for fetching crypto statistics
 */
public interface CryptoStatsDao {

    List<CryptoStat> getStatsForCrypto(String symbol);

    List<CryptoStat> getStatsForCryptoWithMonthRange(String symbol, LocalDate start, LocalDate end);

    Map<String, List<CryptoStat>> getAllCryptoStats();

    Map<String, List<CryptoStat>> getAllCryptoStatsWithMonthRange(LocalDate from, LocalDate to);
}
