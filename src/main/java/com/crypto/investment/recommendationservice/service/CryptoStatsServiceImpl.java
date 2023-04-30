package com.crypto.investment.recommendationservice.service;

import com.crypto.investment.recommendationservice.models.CryptoStat;
import com.crypto.investment.recommendationservice.models.CryptoStatAggregated;
import com.crypto.investment.recommendationservice.repository.CryptoStatsDao;
import com.crypto.investment.recommendationservice.service.comparators.CryptoStatNormalizedRangeComparator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Crypto service for retrieving aggregated crypto statistics
 */
@Service
public class CryptoStatsServiceImpl implements CryptoStatsService {

    @Autowired
    private CryptoStatsDao cryptoStatsDao;
    @Autowired
    private CryptoValidator cryptoValidator;

    /**
     * Calculates aggregated statistic for specified crypto
     * @param symbol - Cryptocurrency symbol to fetch aggregated statistic for.
     *                  If specified crypto is not supported then exception will be thrown
     * @param months - Parameter for specifying the number of last months to aggregate statistic for.
     *                  If {@value `null`} - aggregated statistic will be based on the whole period
     * @return specified crypto aggregated statistic (MAX/MIN/NEWEST/OLDEST){@link CryptoStatAggregated}
     *          for the whole period or for the last number of months
     * @throws CryptoNotSupportedException if specified is crypto is not supported
     */
    @Override
    public Optional<CryptoStatAggregated> getAggregatedStatsForCrypto(String symbol, Integer months) {
        cryptoValidator.validateSupportedCrypto(symbol);
        var currencySymbol = symbol.toUpperCase();
        var cryptoStats = Objects.isNull(months)
                            ? cryptoStatsDao.getStatsForCrypto(currencySymbol)
                            : cryptoStatsDao.getStatsForCryptoWithMonthRange(currencySymbol, nowPlusMonths(-months), nowPlusMonths(1));
        return Optional.ofNullable(aggregateStats(currencySymbol, cryptoStats));
    }

    /**
     * Calculates aggregated statistics for all cryptos
     * @param months - Parameter for specifying the number of last months to aggregate statistic for.
     *                  If {@value `null`} - aggregated statistic will be based on the whole period
     * @return descending sorted set of aggregated statistic (MAX/MIN/NEWEST/OLDEST){@link CryptoStatAggregated}
     *          comparing the normalized range for all cryptos for the whole period or for the last number of months
     */
    @Override
    public SortedSet<CryptoStatAggregated> getAggregatedStatsPerCryptoInDescendingOrder(Integer months) {
        var cryptoStats = Objects.isNull(months)
                            ? cryptoStatsDao.getAllCryptoStats()
                            : cryptoStatsDao.getAllCryptoStatsWithMonthRange(nowPlusMonths(-months), nowPlusMonths(1));
        return cryptoStats.keySet().stream()
                .map(key -> aggregateStats(key, cryptoStats.get(key)))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> new TreeSet<>(new CryptoStatNormalizedRangeComparator())));
    }

    /**
     * Calculates crypto aggregated statistic with the highest normalized range fo specified day
     * @param date - defines specific day to fetch statistic for
     * @return crypto aggregated statistic {@link CryptoStatAggregated} with highest normalized range
     *          for specified day
     */
    @Override
    public Optional<CryptoStatAggregated> getCryptoWithHighestNormalizedRangeForDay(LocalDate date) {
        return cryptoStatsDao.getAllCryptoStatsWithMonthRange(date, date.plusMonths(1))
                .entrySet().stream()
                .map(entry -> aggregateStats(entry.getKey(), filterByDateSpan(entry.getValue(), date, date.plusDays(1))))
                .filter(Objects::nonNull)
                .max(new CryptoStatNormalizedRangeComparator());
    }

    private LocalDate nowPlusMonths(int months) {
        return LocalDate.now(ZoneOffset.UTC).plusMonths(months);
    }

    private List<CryptoStat> filterByDateSpan(List<CryptoStat> cryptoStats, LocalDate start, LocalDate end) {
        return cryptoStats.parallelStream()
                .filter(cr -> cr.getTimestamp().isAfter(start.atStartOfDay())
                                && cr.getTimestamp().isBefore(end.atStartOfDay()))
                .collect(Collectors.toList());
    }

    private CryptoStatAggregated aggregateStats(String symbol, List<CryptoStat> cryptoStats) {
        if (CollectionUtils.isEmpty(cryptoStats)) {
            return null;
        }
        var cryptoStatAggregated = new CryptoStatAggregated(symbol);
        setAggregatedStats(cryptoStatAggregated, cryptoStats, EnumSet.allOf(CryptoStatAggregatedFields.class));
        return cryptoStatAggregated;
    }

    private void setAggregatedStats(CryptoStatAggregated cryptoStatAggregated, List<CryptoStat> cryptoStats, Set<CryptoStatAggregatedFields> fields) {
        fields.forEach(field -> setAggregatedStat(cryptoStatAggregated, cryptoStats, field));
    }

    private void setAggregatedStat(CryptoStatAggregated cryptoStatAggregated, List<CryptoStat> cryptoStats, CryptoStatAggregatedFields field) {
        var cryptoStat = cryptoStats.stream().max(field.getComp());
        cryptoStat.map(CryptoStat::getPrice)
                  .ifPresent(price -> field.getSetter().accept(cryptoStatAggregated, price));
    }
}
