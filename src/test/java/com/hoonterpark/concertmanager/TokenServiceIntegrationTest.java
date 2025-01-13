package com.hoonterpark.concertmanager;

import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.repository.TokenRepository;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;



@Transactional
@SpringBootTest
public class TokenServiceIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    private TokenEntity token1;
    private TokenEntity token2;
    private TokenEntity token3;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터 준비
        token1 = TokenEntity.builder()
                .status(TokenStatus.ACTIVE)
                .tokenValue("token1")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        token2 = TokenEntity.builder()
                .status(TokenStatus.PENDING)
                .tokenValue("token2")
                .expiredAt(LocalDateTime.now().plusMinutes(20))
                .build();

        token3 = TokenEntity.builder()
                .status(TokenStatus.RESERVED)
                .tokenValue("token3")
                .expiredAt(LocalDateTime.now().plusMinutes(20))
                .build();

        tokenRepository.save(token1);
        tokenRepository.save(token2);
        tokenRepository.save(token3);
    }

    @Test
    public void testIssueToken() {
        LocalDateTime now = LocalDateTime.now();
        TokenEntity newToken = tokenService.issueToken(now);

        assertThat(newToken).isNotNull();
        assertThat(newToken.getStatus()).isEqualTo(TokenStatus.PENDING);
        assertThat(newToken.getExpiredAt()).isAfter(now);
    }

    @Test
    public void testIsActive() {
        LocalDateTime now = LocalDateTime.now();
        TokenEntity activeToken = tokenService.isActive("token1", now);

        assertThat(activeToken).isNotNull();
        assertThat(activeToken.getTokenValue()).isEqualTo("token1");
    }

    @Test
    public void testGetToken() {
        TokenEntity foundToken = tokenService.getToken("token2");

        assertThat(foundToken).isNotNull();
        assertThat(foundToken.getTokenValue()).isEqualTo("token2");
    }

    @Test
    public void testGetWaitingNumber() {
        int waitingNumber = tokenService.getWaitingNumber("token2");

        assertThat(waitingNumber).isEqualTo(1); // token1이 latest active 토큰이므로 token2의 대기번호는 1
    }

    @Test
    public void testExpireToken() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(15); // 만료 시간을 지나치게 설정
        List<TokenEntity> expiredTokens = tokenService.expireToken(now);

        assertThat(expiredTokens).hasSize(1); // 두 개의 토큰이 만료되어야 함
        assertThat(expiredTokens.get(0).getStatus()).isEqualTo(TokenStatus.EXPIRED);
        assertThat(expiredTokens.get(0).getTokenValue()).isEqualTo(token1.getTokenValue());
    }

    @Test
    public void testActivateToken() {
        LocalDateTime now = LocalDateTime.now();
        List<TokenEntity> activatedTokens = tokenService.activateToken(now);

        assertThat(activatedTokens).contains(token2);
        assertThat(activatedTokens.get(0).getStatus()).isEqualTo(TokenStatus.ACTIVE);
    }

    @Test
    public void testUpdateTokenToReserved() {
        LocalDateTime now = LocalDateTime.now();
        Boolean result = tokenService.updateTokenToReserved("token1", now);

        assertThat(result).isTrue();
        TokenEntity updatedToken = tokenRepository.findByTokenValue("token1").orElseThrow();
        assertThat(updatedToken.getStatus()).isEqualTo(TokenStatus.RESERVED);
    }

    @Test
    public void testUpdateTokenToPaid() {
        tokenService.updateTokenToReserved("token3", LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();
        Boolean result = tokenService.updateTokenToPaid("token3", now);

        assertThat(result).isTrue();
        TokenEntity updatedToken = tokenRepository.findByTokenValue("token3").orElseThrow();
        assertThat(updatedToken.getStatus()).isEqualTo(TokenStatus.PAID);
    }
}
