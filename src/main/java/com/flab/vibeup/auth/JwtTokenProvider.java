package com.flab.vibeup.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {
    // 토큰 발급 / 검증
    private final Key key;  // 서명에 사용할 HMAC SecretKey
    private final long validityMs; // 토큰 유효기간

    public JwtTokenProvider( // application.yml 의 값 주입
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-validity-seconds}") long validitySeconds
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.validityMs = validitySeconds * 1000L;
    }

    // Access Token 발급
    public String createToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMs);
        return Jwts.builder()
                .setSubject(subject)  // email
                .addClaims(claims)    // id, role
                .setIssuedAt(now)     // key issued time
                .setExpiration(exp)   // key expiration time
                .signWith(key, SignatureAlgorithm.HS256)  // HMAC-SHA256 서명
                .compact(); // 최종 JWT Access Token 문자열 생성
    }

    // Token Parsing, 서명 검증
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    // Token Validation (Parse)
    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch(JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 인증 시 email 확인할 때 사용
    public String getSubject(String token) {
        return parse(token).getBody().getSubject();
    }
}
