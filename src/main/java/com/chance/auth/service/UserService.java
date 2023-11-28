package com.chance.auth.service;

import com.chance.auth.contract.OtpService;
import com.chance.auth.domain.req.OtpVerifyRequest;
import com.chance.auth.domain.res.JwtTokenResponse;
import com.chance.auth.entity.User;
import com.chance.auth.exception.DBException;
import com.chance.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtTokenProvider jwtTokenService;

    /**
     * This method handles creation of user with only msisdn.
     *
     * @param msisdn
     * @return
     */
    private Mono<User> saveUserWithOnlyMSISDN(String msisdn) {
        return userRepository
                .save(User.builder().msisdn(msisdn).build())
                .switchIfEmpty(Mono.error(new DBException("User could not created!")));
    }

    /**
     * This method controls user exists or not.
     * According to these statuses creates new user or get exists user and call jwtToken service to generate new token for user.
     *
     * @param otpVerifyRequest
     * @return
     */
    public Mono<JwtTokenResponse> checkVerificationCodeAndRegister(OtpVerifyRequest otpVerifyRequest) {
        return otpService
                .checkVerificationCode(otpVerifyRequest)
                .flatMap(r -> userRepository.findByMsisdn(otpVerifyRequest.msisdn()))
                .switchIfEmpty(this.saveUserWithOnlyMSISDN(otpVerifyRequest.msisdn()))
                .flatMap(user -> jwtTokenService.createToken(user.getMsisdn(), user.getRolesAsListString()))
                .map(s -> JwtTokenResponse.builder().token(s).build());
    }
}
