package offeria.api_gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtUtilsTest {

    private static final String SECRET =
            "test-only-jwt-secret-key-that-is-at-least-32-bytes-long";

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", SECRET);
    }

    @Test
    void extractsUsernameAndRolesFromAuthServiceTokenContract() {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );

        long now = System.currentTimeMillis();

        String token = Jwts.builder()
                .claims(Map.of(
                        "roles",
                        List.of("ROLE_ADMIN", "ROLE_USER")
                ))
                .subject("testuser")
                .issuedAt(new Date(now))
                .expiration(new Date(now + 3600000))
                .signWith(key)
                .compact();

        assertEquals(
                "testuser",
                jwtUtils.extractUsername(token)
        );

        assertEquals(
                List.of("ROLE_ADMIN", "ROLE_USER"),
                jwtUtils.extractRoles(token)
        );
    }

    @Test
    void returnsEmptyRolesWhenClaimIsMissing() {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );

        long now = System.currentTimeMillis();

        String token = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date(now))
                .expiration(new Date(now + 3600000))
                .signWith(key)
                .compact();

        assertEquals(
                List.of(),
                jwtUtils.extractRoles(token)
        );
    }
}