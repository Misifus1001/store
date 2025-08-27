package com.migramer.store.service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${secret.key}")
    private String secretKey;

    @Value("${tiempo.expiracion}")
    private long tiempoExpiracion; 

    private Key getSignInKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String email, Map<String, Object> claims){

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + tiempoExpiracion))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();

    }
  
    public <T> T extractClaim(String token, Function<Claims, T> resolver){

        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();

        return resolver.apply(claims);
    }

    public String extractEmail(String email){
        return extractClaim(email, Claims::getSubject);
    }

    public boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());

    }

    public boolean isTokenValid(String token, String email){
        return (extractEmail(token).equals(email) && ! isTokenExpired(token));
    }
}
