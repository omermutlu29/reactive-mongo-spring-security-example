package com.chance.auth.domain.req;

import lombok.Builder;

@Builder
public record SendOtpRequest(String msisdn) {}
