package com.chance.auth.repository;

import com.chance.auth.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<Boolean> existsByUsername(String username);

    Mono<User> findByUsername(String username);

    Mono<User> findByMsisdn(String msisdn);
}
