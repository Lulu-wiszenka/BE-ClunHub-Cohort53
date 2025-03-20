package com.example.familyplanner.Security.JWT;

import com.example.familyplanner.service.exception.InvalidJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtCore {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.jwtLifeTime}")
    private long jwtLifeTime;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtLifeTime);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token); // ✅ Используем parseClaimsJws, а не parseClaimsJwt

            return true;

        } catch (JwtException e) {
            throw new InvalidJwtException("Invalid JWT token: " + e.getMessage());
        }
    }

    public String getUserNameFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)  // ✅ Используем parseClaimsJws
                .getBody();

        return claims.getSubject();
    }
}
