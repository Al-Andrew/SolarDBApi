package com.solardb.api;

import com.solardb.security.AuthProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final AuthProperties authProperties;
    private final JwtEncoder jwtEncoder;

    public AuthController(AuthProperties authProperties, JwtEncoder jwtEncoder) {
        this.authProperties = authProperties;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/token")
    @Operation(
            summary = "Issue access token",
            description = "Exchanges the configured ingest password for a short-lived JWT (Bearer) used on POST /api/v1/readings."
    )
    public ResponseEntity<Map<String, Object>> token(@Valid @RequestBody TokenRequest request) {
        if (!constantTimeEquals(authProperties.password(), request.password())) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
        }

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(authProperties.tokenTtlSeconds());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("solardb-api")
                .subject("ingest")
                .issuedAt(now)
                .expiresAt(exp)
                .claim("scope", "readings:write")
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", authProperties.tokenTtlSeconds()
        ));
    }

    public record TokenRequest(@NotBlank String password) {}

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        int diff = a.length() ^ b.length();
        int min = Math.min(a.length(), b.length());
        for (int i = 0; i < min; i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}

