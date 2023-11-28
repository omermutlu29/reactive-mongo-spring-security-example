package com.chance.auth.exception;

public class OtpPasswordException extends RuntimeException {
    public OtpPasswordException(String otpCouldNotFound) {
        super(otpCouldNotFound);
    }
}
