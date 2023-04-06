package com.crypto.investment.recommendationservice.service.comparators;

import com.crypto.investment.recommendationservice.models.CryptoStat;

import java.util.Comparator;

public class CryptoStatTimestampComparator implements Comparator<CryptoStat> {
    @Override
    public int compare(CryptoStat cs1, CryptoStat cs2) {
        return cs1.getTimestamp() == null
                ? -1
                : cs2.getTimestamp() == null
                    ? 1
                    : cs1.getTimestamp().compareTo(cs2.getTimestamp());
    }
}
