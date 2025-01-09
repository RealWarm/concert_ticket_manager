package com.hoonterpark.concertmanager.presentation.controller.response;

public class TokenResponse {

    public record Token(
            String token
    ) { }

    public record TokenQueueResponse(
            String token,
            Integer queuePosition
    ) { }

}
