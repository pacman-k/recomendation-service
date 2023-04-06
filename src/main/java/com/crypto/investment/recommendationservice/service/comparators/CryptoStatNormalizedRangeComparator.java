package com.crypto.investment.recommendationservice.service.comparators;

import com.crypto.investment.recommendationservice.models.CryptoStatAggregated;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;

public class CryptoStatNormalizedRangeComparator implements Comparator<CryptoStatAggregated> {
    @Override
    public int compare(CryptoStatAggregated cs1, CryptoStatAggregated cs2) {
        return  cs1.getMaxPrice() == null || cs1.getMinPrice() == null
                    ? -1
                    : cs2.getMaxPrice() == null || cs2.getMinPrice() == null
                        ? 1
                        : calculateNormalization(cs1).compareTo(calculateNormalization(cs2));
    }

    private BigDecimal calculateNormalization(CryptoStatAggregated cs) {
        return cs.getMaxPrice().subtract(cs.getMinPrice()).divide(cs.getMaxPrice(), MathContext.DECIMAL32);
    }
}
