package com.chance.auth.repository;

import com.chance.auth.entity.Otp;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface OtpRepository extends ReactiveMongoRepository<Otp, String> {
    Mono<Otp> findOtpByMsisdnAndVerificationCodeAndCreatedAtBetween(
            String msisdn, String verificationCode, LocalDateTime createdAt, LocalDateTime createdAt2);

    Mono<Boolean> existsByMsisdnAndCreatedAtBetween(String msisdn, LocalDateTime createdAt, LocalDateTime createdAt2);
}
