package com.hoonterpark.concertmanager.domain.service;

import com.hoonterpark.concertmanager.common.error.CustomException;
import com.hoonterpark.concertmanager.common.error.ErrorCode;
import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;


@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {
    private final int ACTIVE_TOKEN_COUNT = 5;
    private final int TOKEN_ACTIVE_TIME = 10;
    private final String WAITING_QUEUE = "token:waiting";
    private final String ACTIVE_QUEUE = "token:active";

    private final RedisTemplate<String, Object> redisTemplate;


    public TokenEntity issueToken(LocalDateTime now) {
        // UUID(uuid4) 로 유일성 확보
        TokenEntity newToken = TokenEntity.create(now, TOKEN_ACTIVE_TIME);
        redisTemplate.opsForZSet().add(WAITING_QUEUE, newToken.getTokenValue(), parseDate(now));
        return newToken;
    }


    public Boolean isActive(String tokenValue) {
        long result = getWaitingNumber(tokenValue);
        if (result == -2L) { // 존재(x) = -2
            throw new CustomException(ErrorCode.NOT_FOUND, "Token does not exist");
        }
        return getWaitingNumber(tokenValue) == -1L ? true : false;
    }


    public Long getWaitingNumber(String tokenValue) {
        // 대기열에 있으면 대기순번 반환
        Long waitingNum = redisTemplate.opsForZSet().rank(WAITING_QUEUE, tokenValue);

        if (waitingNum == null) { // 대기열에 없고
            Double score = redisTemplate.opsForZSet().score(ACTIVE_QUEUE, tokenValue);
            if (score != null) { // 활성화  = -1
                return -1L;
            } else if (score == null) { // 존재(x) = -2
                return -2L;
            }
        }
        return waitingNum;
    }


    // 토큰 활성화(스케줄러)
    // 매번 ACTIVE_TOKEN_COUNT 개의 토큰을 WAITING_QUEUE에서 삭제하고
    // ACTIVE_QUEUE에 (현재시간 + 10분)을 SCORE로 넣는다.
    public void activateToken(LocalDateTime now) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Set<Object> tokensToActivate = zSetOps.range(WAITING_QUEUE, 0, ACTIVE_TOKEN_COUNT);

        if (tokensToActivate != null) {
            long newExpiredTime = parseDate(now.plusMinutes(TOKEN_ACTIVE_TIME));
            for (Object token : tokensToActivate) {
                zSetOps.remove(WAITING_QUEUE, token);
                zSetOps.add(ACTIVE_QUEUE, token, newExpiredTime);
            }
        }
    }


    // 활성화 토큰 만료시킨다(스케줄러)
    public void expireToken(LocalDateTime now) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        // 만료될 토큰 조회
        long currentTime = parseDate(now);
        Set<Object> expiredTokens = zSetOps.rangeByScore(ACTIVE_QUEUE, 0, currentTime);

        if (expiredTokens != null) {
            for (Object token : expiredTokens) {
                zSetOps.remove(ACTIVE_QUEUE, token);
            }
        }
    }


    // 좌석 예약하면서 토큰의 만료시간을 10분 늘려준다.
    public void updateTokenToReserved(String tokenValue, LocalDateTime now) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        long newExpirationTime = parseDate(now.plusMinutes(TOKEN_ACTIVE_TIME));

        Double score = zSetOps.score(ACTIVE_QUEUE, tokenValue);
        if (score != null) {
            zSetOps.add(ACTIVE_QUEUE, tokenValue, newExpirationTime);
        } else {
            throw new CustomException(ErrorCode.NOT_FOUND, "Token does not exist in ACTIVE_QUEUE!");
        }
    }


    // LocalDateTime을 가중치로 써보고싶어서 생각한 꼼수 ㅎㅎ
    private long parseDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return Long.parseLong(dateTime.format(formatter));
    }


}//end
