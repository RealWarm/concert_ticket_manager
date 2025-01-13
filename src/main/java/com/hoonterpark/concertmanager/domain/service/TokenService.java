package com.hoonterpark.concertmanager.domain.service;

import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TokenService {
    private final int LIMIT_ACTIVATE_USER = 30;
    private final int TOKEN_ACTIVE_TIME = 10;
    private final TokenRepository tokenRepository;


    // 토큰을 발행한다
    public TokenEntity makeToken(LocalDateTime now) {
        return tokenRepository.save(makeToken(now, TOKEN_ACTIVE_TIME));
    }//issueToken

    // 빌더가 안티패턴이라고 생각함
    // 엔티티 생성은 Entity안에 넣으면 Non-static 에러가 떠서 안됨
    // 그래서 아래와 같은 생성 함수로 작성함
    public TokenEntity makeToken(LocalDateTime now, int tokenActiveTime){
        UUID uuid4 = UUID.randomUUID();
        TokenEntity newToken = TokenEntity.builder()
                .status(TokenStatus.PENDING)
                .tokenValue(uuid4.toString())
                .expiredAt(now.plusMinutes(tokenActiveTime))
                .build();
        return newToken;
    }//issueToken

    // 토큰검증
    public TokenEntity isActive(String tokenValue, LocalDateTime now) {
        TokenEntity myToken = tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰 입니다."));
        myToken.isActive(now);
        return myToken;
    }//getToken

    // 대기열 조회 1-1
    // 토큰의 상태를 조회한다
    public TokenEntity getToken(String tokenValue) {
        return tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰 입니다."));
    }//getToken


    // 대기열 조회 1-2
    // 토큰의 대기순번을 조회한다.
    public int getWaitingNumber(String tokenValue) {
        // (가장 최신의 ACTIVE 상태인 토큰 rno - 현재 토큰의 rno)
        TokenEntity latestActive = tokenRepository.findLatestActiveToken()
                .orElseThrow(() -> new IllegalArgumentException("활성된 토큰이 없습니다."));
        TokenEntity myToken = tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰 입니다."));
        Long myWaitinNumber = myToken.getId() - latestActive.getId();
        return myWaitinNumber >= 0 ? myWaitinNumber.intValue() : 0;
    }//getWaitingNumber


    // 토큰 만료시킨다(스케줄러)
    // PENDING, ACTIVE, RESERVED 인 토큰중에서
    // ExpiredAt을 지난 토큰의 상태를 EXPIRED로 바꾼다.
    public List<TokenEntity> expireToken(LocalDateTime now) {
        // PENDING, ACTIVE, RESERVED 상태인 토큰 모두 수집
        List<TokenStatus> statuses = Arrays.asList(TokenStatus.PENDING, TokenStatus.ACTIVE, TokenStatus.RESERVED);
        List<TokenEntity> tokens = tokenRepository.findByStatusIn(statuses);

        // 만료시킨 토큰만 필터링
        List<TokenEntity> expiredTokens = tokens.stream()
                .filter(token -> token.expireToken(now))
                .collect(Collectors.toList());

        expiredTokens.forEach(token -> tokenRepository.save(token)); // 만료시킨 토큰 저장

        return expiredTokens; // 만료시킨 토큰 리스트를 반환
    }//expireToken


    // 토큰 활성화(스케줄러)
    public List<TokenEntity> activateToken(LocalDateTime now) {
        // 은행창구 :: ACTIVATE 토큰 갯수는 30개다. 모자른 갯수를 찾는다 (30-(현재 Activate 갯수))
        // 현재 Activate 상태인 토큰 갯수 찾기
        List<TokenStatus> activeStatuses = Arrays.asList(TokenStatus.ACTIVE);
        List<TokenEntity> activeTokens = tokenRepository.findByStatusIn(activeStatuses);
        int numberOfUsersToActivate = LIMIT_ACTIVATE_USER - activeTokens.size();

        // STATUS가 PENDING인 가장 오래된 N개의 토큰을 조회(LIMIT N 이런느낌?)
        // 이러면 예) n를 넣어야해 인데 n개중 3개가 시간이 지났으면 n-3개만 넣기 때문에 가득 채우지 못한다.
        // 30개 가져와서 for 문 돌면서 10개 만료하면 반환! 이렇게 할 수 있는데 너무 구현에 억매여 웃긴상황같음
        // 차라리 스케줄러 동작시간을 1분에서 30초로 만들어서 빈번하게 작동하게하는게 맞는거 같음
        List<TokenEntity> tokens = tokenRepository.findTokensToActivate(numberOfUsersToActivate);
        // activating한 토큰만 필터링
        List<TokenEntity> activatedTokens = tokens.stream()
                .filter(token -> token.activateToken(now))
                .collect(Collectors.toList());

        activatedTokens.forEach(token -> tokenRepository.save(token)); //Jpa 쓰면 굳이 더티체킹으로 안써도 될듯함

        return activatedTokens;
    }//activateToken


    // 좌석 예약하면서 토큰값 ACTIVE > RESERVED로 바꾸기
    public Boolean updateTokenToReserved(String tokenValue, LocalDateTime now) {
        TokenEntity myToken = tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰 입니다."));
        return myToken.updateTokenToReserved(now);
    }//updateTokenToReserved

    public Boolean updateTokenToPaid(String tokenValue, LocalDateTime now) {
        TokenEntity myToken = tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰 입니다."));
        return myToken.updateTokenToPaid(now);
    }// updateTokenToPaid


}//end
