package com.crypto.investment.recommendationservice.models;

import java.math.BigDecimal;
import java.util.Objects;

public class CryptoStatAggregated {

    private String symbol;
    private BigDecimal oldestPrice;
    private BigDecimal newestPrice;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;

    public CryptoStatAggregated(String symbol) {
        this.symbol = symbol;
    }

    public CryptoStatAggregated(String symbol, BigDecimal oldestPrice, BigDecimal newestPrice, BigDecimal maxPrice, BigDecimal minPrice) {
        this.symbol = symbol;
        this.oldestPrice = oldestPrice;
        this.newestPrice = newestPrice;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getOldestPrice() {
        return oldestPrice;
    }

    public void setOldestPrice(BigDecimal oldestPrice) {
        this.oldestPrice = oldestPrice;
    }

    public BigDecimal getNewestPrice() {
        return newestPrice;
    }

    public void setNewestPrice(BigDecimal newestPrice) {
        this.newestPrice = newestPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoStatAggregated that = (CryptoStatAggregated) o;
        return Objects.equals(symbol, that.symbol) && Objects.equals(oldestPrice, that.oldestPrice) && Objects.equals(newestPrice, that.newestPrice) && Objects.equals(maxPrice, that.maxPrice) && Objects.equals(minPrice, that.minPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, oldestPrice, newestPrice, maxPrice, minPrice);
    }

    @Override
    public String toString() {
        return "CryptoStatAggregated{" +
                "symbol='" + symbol + '\'' +
                ", oldestPrice=" + oldestPrice +
                ", newestPrice=" + newestPrice +
                ", maxPrice=" + maxPrice +
                ", minPrice=" + minPrice +
                '}';
    }
}
