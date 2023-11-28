package com.chance.auth.contract;

import com.chance.auth.domain.req.OtpVerifyRequest;
import com.chance.auth.domain.res.SendOtpResponse;
import reactor.core.publisher.Mono;

public interface OtpService {
    Mono<SendOtpResponse> sendSms(String msisdn);

    Mono<Boolean> checkVerificationCode(OtpVerifyRequest otpVerifyRequest);
}
