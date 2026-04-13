package com.solardb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SolarDbApiApplication {
    public static void main(String[] args) {
        // Some PaaS providers expose a non-JDBC DB_URL like "postgres://...".
        // Spring's datasource URL must be JDBC ("jdbc:postgresql://..."), so normalize early.
        normalizeDbUrlEnv();
        SpringApplication.run(SolarDbApiApplication.class, args);
    }

    private static void normalizeDbUrlEnv() {
        // If the URL is already explicitly set as a Spring property, trust it.
        if (System.getProperty("spring.datasource.url") != null) return;

        // Prefer SPRING_DATASOURCE_URL, but normalize it if it is non-JDBC (e.g. "postgres://...").
        String springUrl = trimToNull(System.getenv("SPRING_DATASOURCE_URL"));
        if (springUrl != null) {
            String normalized = normalizeToJdbcPostgres(springUrl);
            if (normalized != null) {
                System.setProperty("spring.datasource.url", normalized);
                logResolvedDatasource("SPRING_DATASOURCE_URL", normalized);
            }
            return;
        }

        // Otherwise fall back to DB_URL (commonly "postgres://...") and normalize.
        String dbUrl = trimToNull(System.getenv("DB_URL"));
        if (dbUrl == null) return;

        String normalized = normalizeToJdbcPostgres(dbUrl);
        if (normalized != null) {
            System.setProperty("spring.datasource.url", normalized);
            logResolvedDatasource("DB_URL", normalized);
        }
    }

    private static String normalizeToJdbcPostgres(String url) {
        if (url.startsWith("jdbc:")) return url;
        if (url.startsWith("postgres://")) return "jdbc:postgresql://" + url.substring("postgres://".length());
        if (url.startsWith("postgresql://")) return "jdbc:postgresql://" + url.substring("postgresql://".length());
        return null;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static void logResolvedDatasource(String source, String jdbcUrl) {
        // Avoid printing credentials. This is only to help diagnose platform env wiring.
        String safe = jdbcUrl;
        int at = safe.indexOf('@');
        int scheme = safe.indexOf("://");
        if (scheme >= 0 && at > scheme) {
            safe = safe.substring(0, scheme + 3) + "***@" + safe.substring(at + 1);
        }
        System.out.println("[solardb-api] Resolved spring.datasource.url from " + source + ": " + safe);
    }
}

