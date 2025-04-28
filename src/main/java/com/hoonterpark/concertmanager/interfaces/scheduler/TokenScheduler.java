package com.hoonterpark.concertmanager.interfaces.scheduler;


import com.hoonterpark.concertmanager.application.TokenFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenScheduler {
    private final TokenFacade tokenFacade;


    @Scheduled(fixedRate = 60 * 1000) // 1분 간격 스케줄링
    public void activateTokens(){
        tokenFacade.activateToken(LocalDateTime.now());
    }

    @Scheduled(fixedRate = 60 * 1000)
    public void expireToken(){
        tokenFacade.expireToken(LocalDateTime.now());
    }

}
