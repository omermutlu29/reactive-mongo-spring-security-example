package com.chance.auth.exception;

public class OtpSendException extends RuntimeException {
    public OtpSendException(String otpCouldNotFound) {
        super(otpCouldNotFound);
    }
}
