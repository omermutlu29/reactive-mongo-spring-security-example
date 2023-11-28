package com.chance.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("hello")
public class HomeController {

    @GetMapping
    @PreAuthorize(value = "hasAnyRole('USER','ADMIN','ROLE_USER')")
    public Mono<String> hello() {
        return Mono.just("Hello!");
    }
}
