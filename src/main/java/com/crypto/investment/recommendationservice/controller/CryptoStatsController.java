package com.crypto.investment.recommendationservice.controller;

import com.crypto.investment.recommendationservice.models.CryptoStatAggregated;
import com.crypto.investment.recommendationservice.service.CryptoStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.SortedSet;

/**
 * RestController for retrieving crypto statistic
 */
@RestController
@RequestMapping(value = "/cryptos")
public class CryptoStatsController {

    @Autowired
    private CryptoStatsService service;

    /**
     * RestApi to retrieve aggregated statistic for specified crypto
     * @param symbol - Path variable defines cryptocurrency to fetch aggregated statistic for
     * @param lastMonths - Optional path parameter for specifying the number of last months to fetch statistic for
     * @return specified crypto aggregated statistic (MAX/MIN/NEWEST/OLDEST){@link CryptoStatAggregated}
     *          for the whole period or for the last number of months
     */
    @GetMapping("/{symbol}/stats")
    public ResponseEntity<CryptoStatAggregated> getCryptoStat(@PathVariable String symbol, @RequestParam(required = false, name = "m") Integer lastMonths) {
        return ResponseEntity.of(service.getAggregatedStatsForCrypto(symbol, lastMonths));
    }

    /**
     * RestApi for retrieving descending sorted list of all aggregated crypto statistics,
     * comparing the normalized range (i.e. (max-min)/min)
     * @param lastMonths - Optional path parameter for specifying the number of last months to fetch statistic for
     * @return descending sorted set of aggregated statistic (MAX/MIN/NEWEST/OLDEST){@link CryptoStatAggregated}
     *          for all cryptos for the whole period or for the last number of months
     */
    @GetMapping("/ranking")
    public ResponseEntity<SortedSet<CryptoStatAggregated>> getCryptoStatsOrderedByNormalizedRange(@RequestParam(required = false, name = "m") Integer lastMonths) {
        return ResponseEntity.ok(service.getAggregatedStatsPerCryptoInDescendingOrder(lastMonths));
    }

    /**
     * RestApi for retrieving aggregated crypto statistics with the highest normalized range for specified day
     * @param date - Path variable defines specific day with {@link DateTimeFormat} yyyy-MM-dd to fetch statistic for
     * @return crypto aggregated statistic {@link CryptoStatAggregated} with highest normalized range
     *          for specified day
     */
    @GetMapping("/ranking/{date}")
    public ResponseEntity<CryptoStatAggregated> getCryptoStatWithHighestNormalizedRange(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.of(service.getCryptoWithHighestNormalizedRangeForDay(date));
    }
}
