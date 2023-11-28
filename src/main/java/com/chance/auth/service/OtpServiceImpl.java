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
        Mono<Otp> savedOtp = otpRepository.save(Otp.builder()
                .msisdn(msisdn)
                .verificationCode(this.generateCode())
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

    @Override
    public Mono<Boolean> checkVerificationCode(OtpVerifyRequest otpVerifyRequest) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minusOneMinute = now.minusMinutes(1);
        return otpRepository
                .findOtpBymsisdnAndVerificationCode(otpVerifyRequest.msisdn(), otpVerifyRequest.verificationCode())
                .switchIfEmpty(Mono.error(new OtpPasswordException(OTP_COULD_NOT_FOUND)))
                .map(Otp::getCreatedAt)
                .map(createdAt -> createdAt.isBefore(minusOneMinute))
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new OtpPasswordException(OTP_OUT_OF_TIME_ERROR)));
    }
}
