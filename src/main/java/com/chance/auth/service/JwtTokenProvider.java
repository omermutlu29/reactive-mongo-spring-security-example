package com.chance.auth.service;

import com.chance.auth.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class JwtTokenProvider {

    @Value("${springbootwebfluxjjwt.jjwt.secret}")
    private String secretKey;

    @Value("${springbootwebfluxjjwt.jjwt.expiration}")
    private long minutes;

    public Mono<String> createToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + (1000 * 60 * minutes));

        return Mono.just(Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact());
    }

    public Mono<String> resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return Mono.just(bearerToken.substring(7));
        }
        return Mono.empty();
    }

    public Mono<Boolean> validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return Mono.just(!claimsJws.getBody().getExpiration().before(new Date()));
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            return Mono.error(new JwtAuthenticationException("Expired or invalid JWT token", e));
        }
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Mono<List<GrantedAuthority>> getRoles(String token) {
        return Mono.justOrEmpty(Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(token)
                        .getBody()
                        .get("roles"))
                .map(roles -> (List<String>) roles)
                .map(roleList ->
                        roleList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }

    public Claims getAllClaimsFromToken(String authToken) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken).getBody();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build()
                .parseClaimsJws(token).getBody();

        List<GrantedAuthority> roles = getRoles(token).block();



        User principal = new User(claims.getSubject(), "", roles);

        return new UsernamePasswordAuthenticationToken(principal, token, roles);
    }
}
