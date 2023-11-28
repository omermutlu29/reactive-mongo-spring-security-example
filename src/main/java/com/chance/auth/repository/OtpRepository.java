package com.chance.auth.repository;

import com.chance.auth.entity.Otp;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface OtpRepository extends ReactiveMongoRepository<Otp, String> {
    Mono<Otp> findOtpBymsisdnAndVerificationCode(String msisdn, String verificationCode);
}
