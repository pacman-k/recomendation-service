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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        var fileNamePatterns = List.of(getFileNamePattern(symbol));
        return retrieveCryptoStats(fileNamePatterns).collect(Collectors.toList());
    }

    @Override
    public List<CryptoStat> getStatsForCryptoWithMonthRange(String symbol, LocalDate start, LocalDate end) {
        var fileNamePatterns = getFileNamePatternsForRange(symbol, start, end);
        return retrieveCryptoStats(fileNamePatterns).collect(Collectors.toList());
    }

    /**
     * Retrieve all crypto statistics for all supported cryptos defined in {@link DaoConfig}
     * @return Map of all crypto symbols paired with its statistics
     */
    @Override
    public Map<String, List<CryptoStat>> getAllCryptoStats() {
        var fileNamePatterns = config.getSupportedCryptos().stream()
                .map(this::getFileNamePattern)
                .collect(Collectors.toList());
        return retrieveCryptoStats(fileNamePatterns).collect(Collectors.groupingBy(CryptoStat::getSymbol));
    }

    @Override
    public Map<String, List<CryptoStat>> getAllCryptoStatsWithMonthRange(LocalDate from, LocalDate to) {
        var fileNamePatterns = config.getSupportedCryptos().stream()
                .map(symbol -> getFileNamePatternsForRange(symbol, from, to))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return retrieveCryptoStats(fileNamePatterns).collect(Collectors.groupingBy(CryptoStat::getSymbol));
    }

    private Stream<CryptoStat> retrieveCryptoStats(Collection<Pattern> fileNamePatterns) {
        return getFilePaths(fileNamePatterns).parallelStream()
                        .map(this::readCryptoStatsFromFile)
                        .flatMap(Collection::stream);
    }

    private List<Path> getFilePaths(Collection<Pattern> fileNamePatterns) {
        try (var stream = openPathStream(fileNamePatterns)) {
           return stream.collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error("error during reading file");
            throw new CSVDaoReadException("Internal Server Error");
        }
    }

    private Stream<Path> openPathStream(Collection<Pattern> fileNamePatterns) throws IOException {
        return Files.find(config.getDatasourcePath(), 1,
                          (path, attr) -> Files.isRegularFile(path) && Files.isReadable(path) && matchFile(fileNamePatterns, path));
    }

    private boolean matchFile(Collection<Pattern> fileNamePatterns, Path file) {
        return fileNamePatterns.stream()
                .map(Pattern::asPredicate)
                .anyMatch(s -> s.test(file.getFileName().toString()));
    }

    private List<CryptoStat> readCryptoStatsFromFile(Path pathToFile) {
        try {
            return Files.readAllLines(pathToFile).stream()
                          .skip(1)
                          .map(this::parseLineToCryptoStat)
                          .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error("error during reading file: " + pathToFile, e);
            throw new CSVDaoReadException("Internal Server Error");
        }
    }

    private CryptoStat parseLineToCryptoStat(String line) {
        var cryptoStatFields = line.strip().split(",");
        return new CryptoStat(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(cryptoStatFields[0])), ZoneOffset.UTC),
                              cryptoStatFields[1],
                              new BigDecimal(cryptoStatFields[2]));
    }

    private List<Pattern> getFileNamePatternsForRange(String symbol, LocalDate from, LocalDate to) {
        var filePatterns = new ArrayList<Pattern>();
        for (var start = from; start.isBefore(to); start = start.plusMonths(1)) {
            var pattern = getFileNamePattern(symbol, start.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            filePatterns.add(pattern);
        }
        return filePatterns;
    }

    private Pattern getFileNamePattern(String symbol) {
        return getFileNamePattern(symbol, "\\d{4}-\\d{2}");
    }

    private Pattern getFileNamePattern(String symbol, String date) {
        var fileNamePattern = config.getFilePattern();
        fileNamePattern = fileNamePattern.replace(CSVDaoConfig.CRYPTO_PLACEHOLDER, symbol);
        fileNamePattern = fileNamePattern.replace(CSVDaoConfig.DATE_PLACEHOLDER, date);
        fileNamePattern = fileNamePattern.replaceAll("\\.", "\\\\.");
        return Pattern.compile(fileNamePattern);
    }
}