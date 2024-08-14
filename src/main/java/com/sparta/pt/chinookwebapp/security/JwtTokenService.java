package com.sparta.pt.chinookwebapp.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    public String generateToken(String username, Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        Set<String> prefixedRoles = roles.stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .collect(Collectors.toSet());
        claims.put("roles", prefixedRoles);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 2147483647)) // max int duration (roughly 25 days)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}