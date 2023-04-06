package com.crypto.investment.recommendationservice.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class CryptoStat {
    private String symbol;
    private LocalDateTime timestamp;
    private BigDecimal price;

    public CryptoStat(LocalDateTime timestamp, String symbol, BigDecimal price) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoStat that = (CryptoStat) o;
        return Objects.equals(symbol, that.symbol) && Objects.equals(timestamp, that.timestamp) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, timestamp, price);
    }

    @Override
    public String toString() {
        return "CryptoStat{" +
                "symbol='" + symbol + '\'' +
                ", timestamp=" + timestamp +
                ", price=" + price +
                '}';
    }
}
