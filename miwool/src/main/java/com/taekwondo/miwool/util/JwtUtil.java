package com.taekwondo.miwool.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    
    private final SecretKey key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;
    
    public JwtUtil(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-validity}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity) {
        
        byte[] keyBytes = Base64.getDecoder().decode(Base64.getEncoder().encodeToString(secretKey.getBytes()));
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }
    
    /**
     * Access Token 생성
     */
    public String createAccessToken(String dojangCode, String dojangId, String dojangName) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidity);
        
        return Jwts.builder()
                .subject(dojangId)
                .claim("dojangCode", dojangCode)
                .claim("dojangId", dojangId)
                .claim("dojangName", dojangName)
                .claim("tokenType", "access")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
    
    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(String dojangCode, String dojangId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenValidity);
        
        return Jwts.builder()
                .subject(dojangId)
                .claim("dojangCode", dojangCode)
                .claim("tokenType", "refresh")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
    
    /**
     * 토큰에서 Claims 추출
     */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    
    /**
     * 토큰에서 사용자 ID 추출
     */
    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }
    
    /**
     * 토큰에서 dojangCode 추출
     */
    public String getDojangCode(String token) {
        return parseClaims(token).get("dojangCode", String.class);
    }
    
    /**
     * 토큰에서 dojangName 추출
     */
    public String getDojangName(String token) {
        return parseClaims(token).get("dojangName", String.class);
    }
    
    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
    
    /**
     * 토큰 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}