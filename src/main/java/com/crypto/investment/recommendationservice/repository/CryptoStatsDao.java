package com.crypto.investment.recommendationservice.repository;

import com.crypto.investment.recommendationservice.models.CryptoStat;

import java.util.List;
import java.util.Map;

/**
 * Dao interface for fetching crypto statistics
 */
public interface CryptoStatsDao {

    List<CryptoStat> getStatsForCrypto(String symbol);

    Map<String, List<CryptoStat>> getAllCryptoStats();
}
