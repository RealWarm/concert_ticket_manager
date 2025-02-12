package com.hoonterpark.concertmanager.application;


import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.application.result.TokenResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class TokenFacade {
    private final UserService userService;
    private final TokenService tokenService;


    public TokenResult.TokenQueue issueToken(Long userId, LocalDateTime now) {
        // 유저의 아이디가 유효한지 확인한다
        userService.findById(userId);

        // 토큰을 발행한다.
        TokenEntity newToken = tokenService.issueToken(now);
        int waitingNumber = tokenService.getWaitingNumber(newToken.getTokenValue()).intValue();

        return new TokenResult.TokenQueue(newToken.getTokenValue(), waitingNumber);
    }


    public TokenResult.TokenQueue getQueueToken(String tokenValue){
        // 토큰 대기열 반환
        int waitingNumber = tokenService.getWaitingNumber(tokenValue).intValue();

        return new TokenResult.TokenQueue(tokenValue, waitingNumber);
    }


    // 토큰 활성화(스케줄러)
    public void activateToken(LocalDateTime now){
        tokenService.activateToken(now);
    }


    // 토큰 만료(스케줄러)
    public void expireToken(LocalDateTime now){
        tokenService.expireToken(now);
    }


}//end