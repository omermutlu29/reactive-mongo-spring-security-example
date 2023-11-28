package com.chance.auth.domain.req;

import java.util.Date;
import lombok.Builder;

@Builder
public record OtpVerifyRequest(String name, String surname, Date birthdate, String msisdn, String verificationCode) {}
