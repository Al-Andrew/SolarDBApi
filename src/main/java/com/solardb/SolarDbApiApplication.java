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
        // If the URL is already provided via Spring property/env, leave it alone.
        if (System.getProperty("spring.datasource.url") != null) return;
        if (System.getenv("SPRING_DATASOURCE_URL") != null && !System.getenv("SPRING_DATASOURCE_URL").isBlank()) return;

        String dbUrl = System.getenv("DB_URL");
        if (dbUrl == null || dbUrl.isBlank()) return;

        String normalized = normalizeToJdbcPostgres(dbUrl.trim());
        if (normalized == null) return;

        System.setProperty("spring.datasource.url", normalized);
    }

    private static String normalizeToJdbcPostgres(String url) {
        if (url.startsWith("jdbc:")) return url;
        if (url.startsWith("postgres://")) return "jdbc:postgresql://" + url.substring("postgres://".length());
        if (url.startsWith("postgresql://")) return "jdbc:postgresql://" + url.substring("postgresql://".length());
        return null;
    }
}

