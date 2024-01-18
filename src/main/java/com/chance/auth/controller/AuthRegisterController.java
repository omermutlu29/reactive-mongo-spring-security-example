package com.chance.auth.controller;

import com.chance.auth.contract.OtpService;
import com.chance.auth.domain.req.OtpVerifyRequest;
import com.chance.auth.domain.req.SendOtpRequest;
import com.chance.auth.domain.res.JwtTokenResponse;
import com.chance.auth.domain.res.SendOtpResponse;
import com.chance.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("auth/register")
@RestController
@RequiredArgsConstructor
public class AuthRegisterController {

    private final OtpService otpService;

    private final UserService userService;
    @PostMapping("send-otp")
    public Mono<SendOtpResponse> sendSms(@RequestBody SendOtpRequest sendOtpRequest) {
        return otpService.sendSms(sendOtpRequest.msisdn());
    }
    @PostMapping("verify-otp")
    public Mono<JwtTokenResponse> verify(@RequestBody OtpVerifyRequest otpVerifyRequest) {
        return userService.checkVerificationCodeAndRegister(otpVerifyRequest);
    }
}
