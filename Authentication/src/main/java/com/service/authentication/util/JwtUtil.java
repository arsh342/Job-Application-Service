package com.service.authentication.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret:bXlTZWNyZXRLZXlGb3JKb2JQb3J0YWxBdXRoZW50aWNhdGlvblNlcnZpY2UyMDI1}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 24 hours
    private long expiration;
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(String username, Long userId, String userType, Long externalUserId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userType", userType);
        claims.put("externalUserId", externalUserId);
        return createToken(claims, username);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
    
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }
    
    public String extractUserType(String token) {
        return extractClaim(token, claims -> claims.get("userType", String.class));
    }
    
    public Long extractExternalUserId(String token) {
        return extractClaim(token, claims -> claims.get("externalUserId", Long.class));
    }
    
    private SecretKey getSignKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            // If decoding fails, use the secret directly
            byte[] keyBytes = secret.getBytes();
            if (keyBytes.length < 32) {
                byte[] expandedKey = new byte[32];
                System.arraycopy(keyBytes, 0, expandedKey, 0, Math.min(keyBytes.length, 32));
                keyBytes = expandedKey;
            }
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }
}
