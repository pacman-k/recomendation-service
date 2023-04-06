package com.crypto.investment.recommendationservice.repository;

import com.crypto.investment.recommendationservice.models.CryptoStat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ReadRepository for retrieving crypto statistics from CSV files
 * with source defined in {@link CSVDaoConfig}
 */
@Repository
public class CryptoStatsCSVDao implements CryptoStatsDao {

    private static final Logger LOGGER = LogManager.getLogger(CryptoStatsCSVDao.class);

    @Autowired
    private CSVDaoConfig config;

    /**
     * Retrieve all crypto statistics for provided crypto symbol
     * @param symbol - cryptocurrency symbol to read statistics for
     * @return list of {@link CryptoStat} or empty list if no data found
     */
    @Override
    public List<CryptoStat> getStatsForCrypto(String symbol) {
        return retrieveCryptoStats(symbol).getValue();
    }

    /**
     * Retrieve all crypto statistics for all supported cryptos defined in {@link DaoConfig}
     * @return Map of all crypto symbols paired with its statistics
     */
    @Override
    public Map<String, List<CryptoStat>> getAllCryptoStats() {
        return retrieveCryptoStats(config.getSupportedCryptos());
    }

    private Map<String, List<CryptoStat>> retrieveCryptoStats(Set<String> symbols) {
        return symbols.parallelStream()
                    .map(this::retrieveCryptoStats)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<String, List<CryptoStat>> retrieveCryptoStats(String symbol) {
        var path = config.getDatasourcePath().resolve(getFileName(symbol));
        return new AbstractMap.SimpleEntry<>(symbol, readCryptoStatsFromFile(path));
    }

    private List<CryptoStat> readCryptoStatsFromFile(Path pathToFile) {
        try {
            return Files.exists(pathToFile)
                    ? Files.readAllLines(pathToFile).stream()
                          .skip(1)
                          .map(this::parseLineToCryptoStat)
                          .collect(Collectors.toList())
                    : Collections.emptyList();
        } catch (IOException ex) {
            LOGGER.error("error during reading file: " + pathToFile, ex);
            throw new CSVDaoReadException("Internal Server Error");
        }
    }

    private CryptoStat parseLineToCryptoStat(String line) {
        var cryptoStatFields = line.strip().split(",");
        return new CryptoStat(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(cryptoStatFields[0])), ZoneOffset.UTC),
                                cryptoStatFields[1],
                                new BigDecimal(cryptoStatFields[2]));
    }

    private String getFileName(String symbol) {
        return config.getFilePattern().replace(CSVDaoConfig.CRYPTO_PLACEHOLDER, symbol);
    }
}