package com.crypto.investment.recommendationservice.repository;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Set;

@Component
@ConfigurationProperties("csv-dao-config")
public class CSVDaoConfig implements DaoConfig {
    public static String CRYPTO_PLACEHOLDER = "$SYMBOL";

    private Path datasourcePath;
    private String filePattern;
    private Set<String> supportedCryptos;

    public Path getDatasourcePath() {
        return datasourcePath;
    }

    public void setDatasourcePath(Path datasourcePath) {
        this.datasourcePath = datasourcePath;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public Set<String> getSupportedCryptos() {
        return supportedCryptos;
    }

    public void setSupportedCryptos(Set<String> supportedCryptos) {
        this.supportedCryptos = supportedCryptos;
    }
}
