package com.chance.auth.domain.res;

import lombok.Builder;

@Builder
public class AbstractResponse {
    private Boolean isSuccess;
    private String message;
}
