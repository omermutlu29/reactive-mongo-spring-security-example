package com.chance.auth.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Document(collection = "otp_pass")
public class Otp {
    @Id
    private final String id;

    private String msisdn;
    private String verificationCode;
    private LocalDateTime createdAt;
}
