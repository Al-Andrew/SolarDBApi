package com.solardb.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "solardb.auth")
public record AuthProperties(
        String password,
        String jwtSecret,
        long tokenTtlSeconds
) {}

