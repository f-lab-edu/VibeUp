package com.flab.vibeup.auth;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // 테스트용 시크릿키 : 256bit이상 -> Base64 encoding
        String raw = "aUCYJ7HwUTSjUqs6dvbZxu6Cf+XyOkO3yhDGT3LTwHc=";
        String base64 = Base64.getEncoder().encodeToString(raw.getBytes());
        long validitySeconds = 3600;
        jwtTokenProvider = new JwtTokenProvider(base64, validitySeconds);
    }

    @Test
    void create_and_parse_and_validate() {
        String subject = "hyeonsuns@kakao.com";
        Map<String, Object> claims = Map.of("role", "USER", "uid", 123L);

        String token = jwtTokenProvider.createToken(subject, claims);

        assertThat(jwtTokenProvider.validate(token)).isTrue();
        assertThat(jwtTokenProvider.getSubject(token)).isEqualTo(subject);

        Jws<Claims> parsed = jwtTokenProvider.parse(token);
        assertThat(parsed.getBody().get("role", String.class)).isEqualTo("USER");
        assertThat(parsed.getBody().get("uid", Long.class)).isEqualTo(123L);
    }

}