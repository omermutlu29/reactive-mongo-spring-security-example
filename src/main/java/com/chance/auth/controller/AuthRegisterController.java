package com.chance.auth.controller;

import com.chance.auth.contract.OtpService;
import com.chance.auth.domain.req.OtpVerifyRequest;
import com.chance.auth.domain.req.SendOtpRequest;
import com.chance.auth.domain.res.JwtTokenResponse;
import com.chance.auth.domain.res.SendOtpResponse;
import com.chance.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("auth/register")
@RestController
@RequiredArgsConstructor
public class AuthRegisterController {

    private final OtpService otpService;
    private final UserService userService;

    /**
     * first step of sign-in or sign-up
     *
     * @param sendOtpRequest
     * @return
     */
    @PostMapping("send-otp")
    public Mono<SendOtpResponse> sendSms(SendOtpRequest sendOtpRequest) {
        return otpService.sendSms(sendOtpRequest.msisdn());
    }

    /**
     * second step of sign-in or -sign-up
     *
     * @param otpVerifyRequest
     * @return
     */
    @PostMapping("verify-otp")
    public Mono<JwtTokenResponse> verify(OtpVerifyRequest otpVerifyRequest) {
        return userService.checkVerificationCodeAndRegister(otpVerifyRequest);
    }
}
