package com.chance.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("hello")
public class HomeController {

    @GetMapping
    public Mono<String> hello(Mono<Authentication> authenticationMono) {
        return authenticationMono.map(authentication -> authentication.getPrincipal().toString());
    }
}
