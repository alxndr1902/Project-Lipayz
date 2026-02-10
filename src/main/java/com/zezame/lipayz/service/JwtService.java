package com.zezame.lipayz.service;

import com.zezame.lipayz.exceptiohandler.exception.UnauthorizedException;
import com.zezame.lipayz.model.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class JwtService {
    private final Key secretKey;
    private final long expireTime;
    private final long refreshTime;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-minutes}") long expireTime,
                      @Value("${jwt.expiration-refresh}") long refreshTime) {
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret));
        this.expireTime = expireTime;
        this.refreshTime = refreshTime;
    }

    public String generateToken(String id, String roleCode) {
        var claims = new HashMap<String, Object>();
        claims.put("id", id);
        claims.put("role", roleCode);
        claims.put("type", "ACCESS");

        var jwtBuilder = Jwts.builder()
                .setClaims(claims)
                .setExpiration(Timestamp.valueOf(LocalDateTime.now().plusMinutes(expireTime)))
                .signWith(secretKey);

        return jwtBuilder.compact();
    }

    public String generateRefreshToken(String id, String roleCode) {
        var claims = new HashMap<String, Object>();
        claims.put("id", id);
        claims.put("role", roleCode);
        claims.put("type", "REFRESH");

        var jwtBuilder = Jwts.builder()
                .setClaims(claims)
                .setExpiration(Timestamp.valueOf(LocalDateTime.now().plusMinutes(refreshTime)))
                .signWith(secretKey);

        return jwtBuilder.compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new UnauthorizedException("EXPIRED_TOKEN");
        } catch (JwtException je) {
            throw new UnauthorizedException("INVALID_TOKEN");
        }
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
