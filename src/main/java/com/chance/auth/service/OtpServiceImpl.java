package com.chance.auth.service;

import static com.chance.auth.Constants.*;

import com.chance.auth.contract.OtpService;
import com.chance.auth.domain.req.OtpVerifyRequest;
import com.chance.auth.domain.res.SendOtpResponse;
import com.chance.auth.entity.Otp;
import com.chance.auth.exception.OtpPasswordException;
import com.chance.auth.exception.OtpSendException;
import com.chance.auth.repository.OtpRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Qualifier("testSms")
@Primary
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;

    @Override
    public Mono<SendOtpResponse> sendSms(String msisdn) {
        Mono<Boolean> booleanMono = checkDoesExistOTP(msisdn);
        return booleanMono
                .filter(Boolean.FALSE::equals)
                .flatMap(r -> sendOtpToUser(msisdn))
                .switchIfEmpty(
                        Mono.error(new OtpSendException("Son 1 dakika içerisinde gönderilmiş bir password mevcut!")));
    }

    private Mono<Boolean> checkDoesExistOTP(String msisdn) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minusOne = now.minusMinutes(1);
        return otpRepository
                .existsByMsisdnAndCreatedAtBetween(msisdn, minusOne, now)
                .cache();
    }

    private Mono<Otp> checkDoesExistOTPWithMsisdnAndVerificationCode(String msisdn, String verificationCode) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minusOne = now.minusMinutes(1);
        return otpRepository
                .findOtpByMsisdnAndVerificationCodeAndCreatedAtBetween(msisdn, verificationCode, minusOne, now)
                .cache();
    }

    @Override
    public Mono<Boolean> checkVerificationCode(OtpVerifyRequest otpVerifyRequest) {
        return checkDoesExistOTPWithMsisdnAndVerificationCode(
                        otpVerifyRequest.msisdn(), otpVerifyRequest.verificationCode())
                .map(r -> Boolean.TRUE)
                .switchIfEmpty(Mono.error(new OtpPasswordException(OTP_COULD_NOT_FOUND)));
    }

    private Mono<SendOtpResponse> sendOtpToUser(String msisdn) {
        Mono<Otp> savedOtp = otpRepository.save(Otp.builder()
                .msisdn(msisdn)
                .verificationCode(this.generateCode())
                .createdAt(LocalDateTime.now())
                .build());
        return savedOtp.map(res ->
                        SendOtpResponse.builder().message(OTP_SENT_SUCCESSFULLY).build())
                .switchIfEmpty(Mono.error(new OtpSendException(OTP_COULD_NOT_SEND)));
    }

    private String generateCode() {
        /*Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);*/
        return "111111";
    }
}
