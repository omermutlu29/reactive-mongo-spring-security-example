package com.chance.auth.config.security;

import com.chance.auth.service.JwtTokenProvider;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = jwtTokenProvider.resolveToken(exchange.getRequest()).block();
        if (StringUtils.hasText(token)
                && Boolean.TRUE.equals(
                this.jwtTokenProvider.validateToken(token).block())) {
            return Mono.fromCallable(() -> this.jwtTokenProvider.getAuthentication(token))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(authentication -> chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
        }

        return chain.filter(exchange);
    }
}
