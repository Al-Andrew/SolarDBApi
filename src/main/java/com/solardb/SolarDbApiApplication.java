package com.solardb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

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
                logConnectivityDiagnostics(normalized);
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
            logConnectivityDiagnostics(normalized);
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

    private static void logConnectivityDiagnostics(String jdbcUrl) {
        String enable = trimToNull(System.getenv("DB_CONNECTIVITY_DIAG"));
        if (enable == null || !(enable.equalsIgnoreCase("true") || enable.equals("1") || enable.equalsIgnoreCase("yes"))) {
            return;
        }

        HostPort hp = tryParseHostPortFromJdbc(jdbcUrl);
        if (hp == null) {
            System.out.println("[solardb-api] DB connectivity diag: unable to parse host/port from JDBC url");
            return;
        }

        try {
            InetAddress[] addrs = InetAddress.getAllByName(hp.host());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < addrs.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(addrs[i].getHostAddress());
            }
            System.out.println("[solardb-api] DB connectivity diag: " + hp.host() + " resolves to [" + sb + "]");
        } catch (Exception e) {
            System.out.println("[solardb-api] DB connectivity diag: DNS resolution failed for " + hp.host() + " (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
            return;
        }

        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(hp.host(), hp.port()), 2000);
            System.out.println("[solardb-api] DB connectivity diag: TCP connect OK to " + hp.host() + ":" + hp.port());
        } catch (Exception e) {
            System.out.println("[solardb-api] DB connectivity diag: TCP connect FAILED to " + hp.host() + ":" + hp.port() + " (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
        }
    }

    private static HostPort tryParseHostPortFromJdbc(String jdbcUrl) {
        // Expected: jdbc:postgresql://host:port/db?params...
        String s = jdbcUrl;
        if (!s.startsWith("jdbc:")) return null;
        s = s.substring("jdbc:".length());
        if (!s.startsWith("postgresql://")) return null;

        try {
            URI uri = URI.create(s);
            String host = uri.getHost();
            int port = uri.getPort();
            if (host == null || host.isBlank()) return null;
            if (port <= 0) port = 5432;
            return new HostPort(host, port);
        } catch (Exception ignored) {
            return null;
        }
    }

    private record HostPort(String host, int port) {}
}

