package com.solardb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.web.util.pattern.PatternParseException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@SpringBootApplication
public class SolarDbApiApplication {
    public static void main(String[] args) {
        // Some PaaS providers expose a non-JDBC DB_URL like "postgres://...".
        // Spring's datasource URL must be JDBC ("jdbc:postgresql://..."), so normalize early.
        normalizeDbUrlEnv();
        normalizeMvcStaticPathPattern();
        logJdbcDiagnostics();
        SpringApplication app = new SpringApplication(SolarDbApiApplication.class);
        app.addListeners(new StartupDiagnosticsListener());
        app.run(args);
    }

    private static void normalizeDbUrlEnv() {
        // If the URL is already explicitly set as a Spring property, trust it.
        if (System.getProperty("spring.datasource.url") != null) return;

        // Prefer SPRING_DATASOURCE_URL, but normalize it if it is non-JDBC (e.g. "postgres://...").
        String springUrl = trimToNull(System.getenv("SPRING_DATASOURCE_URL"));
        if (springUrl != null) {
            JdbcNormalization n = normalizeToJdbcPostgres(springUrl);
            if (n != null) {
                applyNormalization("SPRING_DATASOURCE_URL", n);
            }
            return;
        }

        // Otherwise fall back to DB_URL (commonly "postgres://...") and normalize.
        String dbUrl = trimToNull(System.getenv("DB_URL"));
        if (dbUrl == null) return;

        JdbcNormalization n = normalizeToJdbcPostgres(dbUrl);
        if (n != null) {
            applyNormalization("DB_URL", n);
        }
    }

    private static void applyNormalization(String source, JdbcNormalization n) {
        System.setProperty("spring.datasource.url", n.jdbcUrl());
        // If the platform puts user/pass into DB_URL userinfo, also populate Spring username/password
        // unless they were already provided via env.
        if (trimToNull(System.getenv("SPRING_DATASOURCE_USERNAME")) == null && trimToNull(System.getenv("DB_USERNAME")) == null && trimToNull(System.getenv("DB_USER")) == null) {
            if (n.username() != null) System.setProperty("spring.datasource.username", n.username());
        }
        if (trimToNull(System.getenv("SPRING_DATASOURCE_PASSWORD")) == null && trimToNull(System.getenv("DB_PASSWORD")) == null) {
            if (n.password() != null) System.setProperty("spring.datasource.password", n.password());
        }

        logResolvedDatasource(source, n.jdbcUrl());
        logConnectivityDiagnostics(n.jdbcUrl());
    }

    private static JdbcNormalization normalizeToJdbcPostgres(String url) {
        String u = url.trim();
        if (u.startsWith("jdbc:")) {
            // Ensure we don't have userinfo embedded; the driver expects user/pass as properties.
            String s = u.substring("jdbc:".length());
            if (!s.startsWith("postgresql://")) return new JdbcNormalization(u, null, null);
            URI uri = URI.create(s);
            if (trimToNull(uri.getUserInfo()) == null) return new JdbcNormalization(u, null, null);
            return rebuildFromUri(uri);
        }

        if (u.startsWith("postgres://")) u = "postgresql://" + u.substring("postgres://".length());
        if (!u.startsWith("postgresql://")) return null;

        URI uri = URI.create(u);
        return rebuildFromUri(uri);
    }

    private static JdbcNormalization rebuildFromUri(URI uri) {
        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String path = uri.getRawPath() == null ? "" : uri.getRawPath();
        String query = uri.getRawQuery();

        if (host == null || host.isBlank()) return null;

        String jdbc = "jdbc:postgresql://" + host + ":" + port + path + (query == null || query.isBlank() ? "" : "?" + query);

        String userInfo = trimToNull(uri.getRawUserInfo());
        String user = null;
        String pass = null;
        if (userInfo != null) {
            int idx = userInfo.indexOf(':');
            if (idx >= 0) {
                user = decode(userInfo.substring(0, idx));
                pass = decode(userInfo.substring(idx + 1));
            } else {
                user = decode(userInfo);
            }
        }

        return new JdbcNormalization(jdbc, trimToNull(user), trimToNull(pass));
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
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
    private record JdbcNormalization(String jdbcUrl, String username, String password) {}

    private static void logJdbcDiagnostics() {
        String enable = trimToNull(System.getenv("DB_JDBC_DIAG"));
        if (enable == null || !(enable.equalsIgnoreCase("true") || enable.equals("1") || enable.equalsIgnoreCase("yes"))) {
            return;
        }

        String jdbcUrl = trimToNull(System.getProperty("spring.datasource.url"));
        if (jdbcUrl == null) {
            jdbcUrl = trimToNull(System.getenv("SPRING_DATASOURCE_URL"));
        }
        if (jdbcUrl == null) {
            System.out.println("[solardb-api] DB JDBC diag: spring.datasource.url not set; skipping");
            return;
        }

        String username = firstNonBlank(
                System.getenv("SPRING_DATASOURCE_USERNAME"),
                System.getenv("DB_USERNAME"),
                System.getenv("DB_USER")
        );
        String password = firstNonBlank(
                System.getenv("SPRING_DATASOURCE_PASSWORD"),
                System.getenv("DB_PASSWORD")
        );

        Properties props = new Properties();
        if (username != null) props.setProperty("user", username);
        if (password != null) props.setProperty("password", password);

        // Keep diagnostics quick and informative.
        props.setProperty("loginTimeout", "5");
        props.setProperty("connectTimeout", "5");
        props.setProperty("socketTimeout", "10");

        // If the user configured sslmode via env, pass it explicitly as well.
        String sslmode = trimToNull(System.getenv("DB_SSLMODE"));
        if (sslmode != null) props.setProperty("sslmode", sslmode);

        try {
            DriverManager.setLoginTimeout(5);
            try (Connection ignored = DriverManager.getConnection(jdbcUrl, props)) {
                System.out.println("[solardb-api] DB JDBC diag: JDBC connection OK");
            }
        } catch (Exception e) {
            System.out.println("[solardb-api] DB JDBC diag: JDBC connection FAILED (" + e.getClass().getName() + ": " + e.getMessage() + ")");
            Throwable c = e.getCause();
            int depth = 0;
            while (c != null && depth++ < 8) {
                System.out.println("[solardb-api] DB JDBC diag: caused by (" + c.getClass().getName() + ": " + c.getMessage() + ")");
                c = c.getCause();
            }
        }
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            String t = trimToNull(v);
            if (t != null) return t;
        }
        return null;
    }

    private static void normalizeMvcStaticPathPattern() {
        // Runtime env vars can inject Ant-style patterns (e.g. "/**/*.js") that
        // are rejected by Spring's PathPattern parser in Boot 3+.
        String configured = firstNonBlank(
                System.getProperty("spring.mvc.static-path-pattern"),
                System.getenv("SPRING_MVC_STATIC_PATH_PATTERN")
        );
        if (configured == null) {
            return;
        }

        try {
            PathPatternParser.defaultInstance.parse(configured);
        } catch (PatternParseException ex) {
            String fallback = "/**";
            System.err.println("[solardb-api] Invalid spring.mvc.static-path-pattern: '" + configured + "' (" + ex.getMessage() + ")");
            System.err.println("[solardb-api] Falling back to safe default: '" + fallback + "'");
            System.setProperty("spring.mvc.static-path-pattern", fallback);
        }
    }
}

