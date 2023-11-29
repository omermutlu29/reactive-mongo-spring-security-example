package com.chance.auth.config.security;

import com.chance.auth.service.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return jwtTokenProvider
                .resolveToken(exchange.getRequest())
                .flatMap(token -> processWithToken(exchange, chain, token))
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Void> processWithToken(ServerWebExchange exchange, WebFilterChain chain, String token) {
        return jwtTokenProvider
                .validateToken(token)
                .flatMap(valid -> processIsValidTokenAccordingToResult(exchange, chain, token, valid));
    }

    private Mono<Void> processIsValidTokenAccordingToResult(
            ServerWebExchange exchange, WebFilterChain chain, String token, Boolean valid) {
        if (Boolean.TRUE.equals(valid)) {
            String username = jwtTokenProvider.getUsername(token);
            return addSecurityContext(exchange, token, username).flatMap(securityContext ->
                    chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))));
        } else {
            return chain.filter(exchange);
        }
    }

    private Mono<SecurityContextImpl> addSecurityContext(ServerWebExchange exchange, String token, String username) {
        return jwtTokenProvider
                .getRoles(token)
                .map(roles -> new UsernamePasswordAuthenticationToken(username, token, roles))
                .map(authenticationToken -> new SecurityContextImpl(authenticationToken))
                .doOnNext(securityContext -> exchange.getAttributes().put(SecurityContext.class.getName(), securityContext));
    }
}