package com.hoonterpark.concertmanager.application.result;

public class TokenResult {

    public record TokenQueue(
            String token,
            Integer queuePosition
    ) { }

}
