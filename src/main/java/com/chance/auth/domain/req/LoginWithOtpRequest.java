package com.chance.auth.domain.req;

import lombok.Builder;

@Builder
public record LoginWithOtpRequest(String msisdn) {}
