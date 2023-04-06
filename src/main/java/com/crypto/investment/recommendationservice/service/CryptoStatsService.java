package com.crypto.investment.recommendationservice.service;

import com.crypto.investment.recommendationservice.models.CryptoStatAggregated;

import java.time.LocalDate;
import java.util.Optional;
import java.util.SortedSet;

public interface CryptoStatsService {

    Optional<CryptoStatAggregated> getAggregatedStatsForCrypto(String symbol, Integer months);
    SortedSet<CryptoStatAggregated> getAggregatedStatsPerCryptoInDescendingOrder(Integer months);
    Optional<CryptoStatAggregated> getCryptoWithHighestNormalizedRangeForDay(LocalDate date);
}
