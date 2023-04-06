package com.crypto.investment.recommendationservice.service.comparators;

import com.crypto.investment.recommendationservice.models.CryptoStat;

import java.util.Comparator;

public class CryptoStatPriceComparator implements Comparator<CryptoStat> {
    @Override
    public int compare(CryptoStat cs1, CryptoStat cs2) {
        return cs1.getPrice() == null
                ? -1
                : cs2.getPrice() == null
                    ? 1
                    : cs1.getPrice().compareTo(cs2.getPrice());
    }
}
