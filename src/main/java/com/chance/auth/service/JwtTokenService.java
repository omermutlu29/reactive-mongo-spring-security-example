package com.chance.auth.service;

import com.chance.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public class JwtTokenService {

    @Value("${jwt.key")
    private String jwtSecret;

    @Value("${jwt.expire.second")
    private Integer expireSecond;

    public Mono<String> generateJwtToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("omer", "mutlu");
        return createToken(claims, user.getMsisdn());
    }

    public Mono<Boolean> validateToken(String token, UserDetails userDetails) {
        Mono<String> username = Mono.fromCallable(() -> extractUser(token)).cache();
        Mono<Date> expirationDate =
                Mono.fromCallable(() -> extractExpiration(token)).cache();
        return Mono.zip(username, expirationDate)
                .map(tuple2 -> userDetails.getUsername().equals(tuple2.getT1())
                        && tuple2.getT2().after(new Date()))
                .switchIfEmpty(Mono.just(Boolean.FALSE));
    }

    private String extractUser(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    private Date extractExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    private Mono<String> createToken(Map<String, Object> claims, String userName) {
        return Mono.just(Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * expireSecond))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact());
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
