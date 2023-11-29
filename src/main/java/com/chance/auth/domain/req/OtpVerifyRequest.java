package com.chance.auth.domain.req;

import lombok.Builder;

@Builder
public record OtpVerifyRequest(String msisdn, String verificationCode) {}
