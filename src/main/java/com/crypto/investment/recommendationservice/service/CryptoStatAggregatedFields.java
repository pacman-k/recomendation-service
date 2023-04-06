package com.crypto.investment.recommendationservice.service;

import com.crypto.investment.recommendationservice.models.CryptoStat;
import com.crypto.investment.recommendationservice.models.CryptoStatAggregated;
import com.crypto.investment.recommendationservice.service.comparators.CryptoStatPriceComparator;
import com.crypto.investment.recommendationservice.service.comparators.CryptoStatTimestampComparator;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.function.BiConsumer;

public enum CryptoStatAggregatedFields {
    MAX(new CryptoStatPriceComparator(), CryptoStatAggregated::setMaxPrice),
    MIN((new CryptoStatPriceComparator()).reversed(), CryptoStatAggregated::setMinPrice),
    NEWEST(new CryptoStatTimestampComparator(), CryptoStatAggregated::setNewestPrice),
    OLDEST((new CryptoStatTimestampComparator()).reversed(), CryptoStatAggregated::setOldestPrice);

    private final Comparator<CryptoStat> comp;
    private final BiConsumer<CryptoStatAggregated, BigDecimal> setter;
    CryptoStatAggregatedFields(Comparator<CryptoStat> comp, BiConsumer<CryptoStatAggregated, BigDecimal> setter) {
        this.comp = comp;
        this.setter = setter;
    }

    public Comparator<CryptoStat> getComp() {
        return comp;
    }

    public BiConsumer<CryptoStatAggregated, BigDecimal> getSetter() {
        return setter;
    }
}
