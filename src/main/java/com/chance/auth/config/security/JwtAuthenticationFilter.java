package com.chance.auth.config.security;

import com.chance.auth.service.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
                .flatMap(token -> jwtTokenProvider
                        .validateToken(token)
                        .flatMap(valid -> valid
                                ? jwtTokenProvider
                                        .getUsername(token)
                                        .flatMap(username -> jwtTokenProvider
                                                .getRoles(token)
                                                .map(roles ->
                                                        new UsernamePasswordAuthenticationToken(username, null, roles))
                                                .map(SecurityContextImpl::new)
                                                .doOnNext(securityContext -> exchange.getAttributes()
                                                        .put(SecurityContext.class.getName(), securityContext)))
                                        .then(chain.filter(exchange))
                                : chain.filter(exchange)))
                .switchIfEmpty(chain.filter(exchange));
    }
}
