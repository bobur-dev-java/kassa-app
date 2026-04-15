package com.company.kassa.config.security.jwt;


import com.company.kassa.config.security.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProperties;


    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid JWT token");
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }


    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public Long extractYattId(String jwt) {
        return extractClaim(jwt, claims ->
                claims.get("yattId", Long.class)
        );
    }

    public boolean isAccessToken(String jwt) {
        String type = extractClaim(jwt, claims ->
                claims.get("tokenType", String.class)
        );
        return "access".equals(type);
    }

    public boolean isAccessTokenValid(String jwt, UserPrincipal principal) {
        Claims claims = extractAllClaims(jwt);

        return claims.getSubject().equals(principal.getUsername())
                && claims.get("yattId", Long.class).equals(principal.getYattId())
                && "access".equals(claims.get("tokenType", String.class))
                && claims.getExpiration().after(new Date());
    }

    public boolean isRefreshTokenValid(String jwt, UserPrincipal principal) {
        Claims claims = extractAllClaims(jwt);

        return claims.getSubject().equals(principal.getUsername())
                && claims.get("yattId", Long.class).equals(principal.getYattId())
                && "refresh".equals(claims.get("tokenType"))
                && claims.getExpiration().after(new Date());
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateAccessToken(String username, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("tokenType", "access");
        return buildToken(
                claims,
                username,
                jwtProperties.getAccessTokenExpiration()
        );
    }

    public String generateRefreshToken(String username, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("tokenType", "refresh");
        return buildToken(
                claims,
                username,
                jwtProperties.getRefreshTokenExpiration()
        );
    }

    public String generateTempToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTempToken(String token) {
        Claims claims = extractAllClaims(token);
        return "MFA_PENDING".equals(claims.get("type"));
    }

    private String buildToken(Map<String, Object> extraClaims, String username, Long expiration) {
        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims ->
                claims.get("userId", Long.class)
        );
    }

    public String extractRole(String token) {
        return extractClaim(token, claims ->
                claims.get("role", String.class)
        );
    }
}
