package com.hoonterpark.concertmanager.presentation.controller.response;

import com.hoonterpark.concertmanager.domain.enums.TokenStatus;

public class TokenResponse {

    public record Token(
            String token,
            Integer queuePosition
    ) { }

    public record TokenQueueResponse(
            TokenStatus tokenStatus,
            Integer queuePosition
    ) { }

}
