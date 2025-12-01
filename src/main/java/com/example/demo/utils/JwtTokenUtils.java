package com.example.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
public class JwtTokenUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;


    public JwtTokenUtils(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.lifetime}") Duration jwtLifetime) {
        this.secret = secret;
        this.jwtLifetime = jwtLifetime;
    }

    public String generateToken (UserDetails userDetails) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); // Декодируем секрет из Base64

        var rolesList = userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();


        var issuedDate = new Date();
        var expirationDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());
        return Jwts.builder()
                .claims()
                    .subject(userDetails.getUsername())
                    .issuedAt(issuedDate)
                    .expiration(expirationDate)
                    .add("roles", rolesList)
                .and()
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken (String token) {
        return this.getAllClaimsFromToken(token).getSubject();
    }

    public List<String> getRolesFromToken (String token) {
        Object rolesObj = this.getAllClaimsFromToken(token).get("roles");
        if (rolesObj instanceof List<?>) {
            List<?> rolesList = (List<?>) rolesObj;
            if (rolesList.stream().allMatch(item -> item instanceof String)) {
                return (List<String>) rolesList;
            } else {
                throw new ClassCastException("Not all elements in 'roles' are Strings");
            }
        } else {
            throw new IllegalStateException("'roles' claim is not a List");
        }
    }

    public Claims getAllClaimsFromToken (String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
