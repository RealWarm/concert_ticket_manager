package com.hoonterpark.concertmanager.application;


import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.presentation.controller.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class TokenFacade {
    private final UserService userService;
    private final TokenService tokenService;


    // 신규 토큰 발행
    public TokenResponse.Token issueToken(Long userId, LocalDateTime now) {

        // 유저의 아이디가 유효한지 확인한다
        userService.findById(userId);

        // 토큰을 발행한다.
        TokenEntity newToken = tokenService.makeToken(now);
        int waitingNumber = tokenService.getWaitingNumber(newToken.getTokenValue());

        return new TokenResponse.Token(newToken.getTokenValue(), waitingNumber);
    }//issueToken


    // 토큰및 대기열 반환
    public TokenResponse.TokenQueueResponse getQueueToken(String tokenValue){
        // 토큰이 존재하나 확인
        TokenEntity token = tokenService.getToken(tokenValue);

        // 토큰 대기열 반환
        int waitingNumber = tokenService.getWaitingNumber(tokenValue);
        return new TokenResponse.TokenQueueResponse(token.getStatus(), waitingNumber);
    }//getQueueToken


    // 토큰 만료(스케줄러)
    // PENDING, ACTIVE, RESERVED 인 토큰중에서
    // ExpiredAt을 지난 토큰의 상태를 EXPIRED로 바꾼다.
    public void expireToken(LocalDateTime now){
        tokenService.expireToken(now);
    }


    // 토큰 활성화(스케줄러)
    public void activateToken(LocalDateTime now){
        tokenService.activateToken(now);
    }


}//end