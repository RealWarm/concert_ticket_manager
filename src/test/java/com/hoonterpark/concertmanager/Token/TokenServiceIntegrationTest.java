package com.hoonterpark.concertmanager.Token;

import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.repository.TokenRepository;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;


import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
public class TokenServiceIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

//    private TokenEntity token1;
//    private TokenEntity token2;
//    private TokenEntity token3;

    @BeforeEach
    public void setUp() {
//        token1 = TokenEntity.builder()
//                .status(TokenStatus.ACTIVE)
//                .tokenValue("token1")
//                .expiredAt(LocalDateTime.now().plusMinutes(10))
//                .build();
//
//        token2 = TokenEntity.builder()
//                .status(TokenStatus.PENDING)
//                .tokenValue("token2")
//                .expiredAt(LocalDateTime.now().plusMinutes(10))
//                .build();
//
//        token3 = TokenEntity.builder()
//                .status(TokenStatus.RESERVED)
//                .tokenValue("token3")
//                .expiredAt(LocalDateTime.now().plusMinutes(10))
//                .build();
//
//        tokenRepository.save(token1);
//        tokenRepository.save(token2);
//        tokenRepository.save(token3);
    }



    @DisplayName("발행된 토큰의 상태는 PENDING이다.")
    @Test
    public void testIssueToken() {
        LocalDateTime now = LocalDateTime.now();
        TokenEntity newToken = tokenService.makeToken(now);

        assertThat(newToken).isNotNull();
        assertThat(newToken.getStatus()).isEqualTo(TokenStatus.PENDING);
    }


    @DisplayName("")
    @Test
    public void testIsActive() {
        LocalDateTime now = LocalDateTime.now();
        TokenEntity newToken = tokenService.makeToken(now);
        newToken.activateToken(now);


        TokenEntity activeToken = tokenService.isActive(newToken.getTokenValue(), now);

        assertThat(activeToken).isNotNull();
        assertThat(activeToken.getTokenValue()).isEqualTo(newToken.getTokenValue());
    }

//    @Test
//    public void testGetToken() {
//        TokenEntity foundToken = tokenService.getToken("token2");
//
//        assertThat(foundToken).isNotNull();
//        assertThat(foundToken.getTokenValue()).isEqualTo("token2");
//    }
//
//    @Test
//    public void testGetWaitingNumber() {
//        int waitingNumber = tokenService.getWaitingNumber("token2");
//
//        assertThat(waitingNumber).isEqualTo(1); // token1이 latest active 토큰이므로 token2의 대기번호는 1
//    }
//
//    @DisplayName("만료스케줄러 정상작동 테스트")
//    @Test
//    public void testExpireToken() {
//        LocalDateTime now = LocalDateTime.now().plusMinutes(15); // 만료 시간을 지나치게 설정
//        List<TokenEntity> expiredTokens = tokenService.expireToken(now);
//
//        assertThat(expiredTokens).hasSize(3)
//                .extracting("tokenValue", "status")
//                .containsExactlyInAnyOrder(
//                        Tuple.tuple("token1", TokenStatus.EXPIRED),
//                        Tuple.tuple("token2", TokenStatus.EXPIRED),
//                        Tuple.tuple("token3", TokenStatus.EXPIRED));
//    }
//
//
//    @Test
//    public void testActivateToken() {
//        LocalDateTime now = LocalDateTime.now();
//        List<TokenEntity> activatedTokens = tokenService.activateToken(now);
//
//        assertThat(activatedTokens).contains(token2);
//        assertThat(activatedTokens.get(0).getStatus()).isEqualTo(TokenStatus.ACTIVE);
//    }
//
//    @Test
//    public void testUpdateTokenToReserved() {
//        LocalDateTime now = LocalDateTime.now();
//        Boolean result = tokenService.updateTokenToReserved("token1", now);
//
//        assertThat(result).isTrue();
//        TokenEntity updatedToken = tokenRepository.findByTokenValue("token1").orElseThrow();
//        assertThat(updatedToken.getStatus()).isEqualTo(TokenStatus.RESERVED);
//    }
//
//    @Test
//    public void testUpdateTokenToPaid() {
//        tokenService.updateTokenToReserved("token3", LocalDateTime.now());
//
//        LocalDateTime now = LocalDateTime.now();
//        Boolean result = tokenService.updateTokenToPaid("token3", now);
//
//        assertThat(result).isTrue();
//        TokenEntity updatedToken = tokenRepository.findByTokenValue("token3").orElseThrow();
//        assertThat(updatedToken.getStatus()).isEqualTo(TokenStatus.PAID);
//    }
}
