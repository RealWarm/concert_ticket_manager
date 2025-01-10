package com.hoonterpark.concertmanager;

import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.repository.TokenRepository;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

public class TokenServiceUnitTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    private TokenEntity token;
    private TokenEntity newToken;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        newToken = TokenEntity.builder()
                .id(0L)
                .status(TokenStatus.PENDING)
                .tokenValue("newtoken1")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
        token = TokenEntity.builder()
                .id(10L)
                .status(TokenStatus.ACTIVE)
                .tokenValue("token1")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
    }


    @Test
    public void testIssueToken() {
        // Given
        when(tokenRepository.save(any(TokenEntity.class))).thenReturn(newToken);

        // When
        TokenEntity newToken = tokenService.issueToken(LocalDateTime.now());

        // Then
        assertThat(newToken).isNotNull();
        assertThat(newToken.getStatus()).isEqualTo(TokenStatus.PENDING);
        assertThat(newToken.getTokenValue()).isNotNull();
    }


    @Test
    public void testIsActive() {
        // Given
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(token));

        // When
        TokenEntity activeToken = tokenService.isActive("token1", LocalDateTime.now());

        // Then
        assertThat(activeToken).isNotNull();
        assertThat(activeToken.getTokenValue()).isEqualTo("token1");
    }

    @Test
    public void testGetToken() {
        // Given
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(token));

        // When
        TokenEntity foundToken = tokenService.getToken("token1");

        // Then
        assertThat(foundToken).isNotNull();
        assertThat(foundToken.getTokenValue()).isEqualTo("token1");
    }

    @Test
    public void testGetWaitingNumber() {
        // Given
        TokenEntity latestActiveToken = TokenEntity.builder()
                .id(2L) // 최신 ACTIVE 토큰의 ID
                .status(TokenStatus.ACTIVE)
                .tokenValue("token2")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(token));
        when(tokenRepository.findLatestActiveToken()).thenReturn(Optional.of(latestActiveToken));

        // When
        int waitingNumber = tokenService.getWaitingNumber("token1");

        // Then
        assertThat(waitingNumber).isEqualTo(token.getId()-latestActiveToken.getId()); // token1의 ID는 1이고, token2의 ID는 2이므로 대기번호는 1
    }

    @Test
    public void testExpireToken() {
        // Given
        when(tokenRepository.findByStatusIn(any())).thenReturn(List.of(token));

        // When
        List<TokenEntity> expiredTokens = tokenService.expireToken(LocalDateTime.now().plusMinutes(15));

        // Then
        assertThat(expiredTokens).hasSize(1);
        assertThat(expiredTokens.get(0).getStatus()).isEqualTo(TokenStatus.EXPIRED);
    }


    @Test
    public void testActivateToken() {
        // Given
        when(tokenRepository.findByStatusIn(any())).thenReturn(List.of(token));
        when(tokenRepository.findTopNByTokenStatusOrderByExpiredAtAsc(any(), anyInt()))
                .thenReturn(List.of(newToken));

        // When
        List<TokenEntity> activatedTokens = tokenService.activateToken(LocalDateTime.now());

        // Then
        assertThat(activatedTokens).hasSize(1);
        assertThat(activatedTokens.get(0).getStatus()).isEqualTo(TokenStatus.ACTIVE);
    }


    @Test
    public void testUpdateTokenToReserved() {
        // Given
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(token));

        // When
        Boolean result = tokenService.updateTokenToReserved("token1", LocalDateTime.now());

        // Then
        assertThat(result).isTrue();
        assertThat(token.getStatus()).isEqualTo(TokenStatus.RESERVED);
    }


    @Test
    public void testUpdateTokenToPaid() {
        // Given
        TokenEntity testUpdateToken = TokenEntity.builder()
                .status(TokenStatus.RESERVED)
                .tokenValue("testUpdateToken11")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(testUpdateToken));

        // When
        Boolean result = tokenService.updateTokenToPaid("token1", LocalDateTime.now());

        // Then
        assertThat(result).isTrue();
        assertThat(testUpdateToken.getStatus()).isEqualTo(TokenStatus.PAID);
    }
}
