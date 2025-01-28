package com.hoonterpark.concertmanager.domain;

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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TokenServiceUnitTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    private TokenEntity token1;
    private TokenEntity token2;
    private TokenEntity activeButTimeOver;
    private TokenEntity newToken;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        token1 = TokenEntity.builder()
                .id(1L)
                .status(TokenStatus.ACTIVE)
                .tokenValue("token1")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        token2 = TokenEntity.builder()
                .id(2L)
                .status(TokenStatus.EXPIRED)
                .tokenValue("token2")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        activeButTimeOver = TokenEntity.builder()
                .id(3L)
                .status(TokenStatus.ACTIVE)
                .tokenValue("activeButTimeOver")
                .expiredAt(LocalDateTime.now().minusMinutes(10))
                .build();

        newToken = TokenEntity.builder()
                .id(10L)
                .status(TokenStatus.PENDING)
                .tokenValue("newtoken1")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

    }//setUp

    // 통합이 맞는듯...
    @Test
    public void testIssueToken() {
        // Given
        when(tokenRepository.save(any(TokenEntity.class))).thenReturn(newToken);

        // When
        TokenEntity newToken = tokenService.makeToken(LocalDateTime.now());

        // Then
        assertThat(newToken).isNotNull();
        assertThat(newToken.getStatus()).isEqualTo(TokenStatus.PENDING);
        assertThat(newToken.getTokenValue()).isNotNull();
    }

    // 통합이 맞는듯...
    @Test
    public void testIsActive() {
        // Given
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(token1));

        // When
        TokenEntity activeToken = tokenService.isActive("token1", LocalDateTime.now());

        // Then
        assertThat(activeToken).isNotNull();
        assertThat(activeToken.getTokenValue()).isEqualTo("token1");
    }


    @DisplayName("Active 상태지만 유효시간이 지난 토큰을 활성체크하면 에러")
    @Test
    public void testIsActive2() {
        // Given
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(activeButTimeOver));

        // When && Then
        assertThatThrownBy(()->tokenService.isActive("activeButTimeOver", LocalDateTime.now()))
                .isInstanceOf(RuntimeException.class).hasMessage("예약가능한 유효시간이 지났습니다.");
    }


    @DisplayName("만료된 상태의 토큰을 활성화 체크하면 에러")
    @Test
    public void testIsActive3() {
        // Given
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(token2));

        // When && Then
        assertThatThrownBy(()->tokenService.isActive("token2", LocalDateTime.now()))
                .isInstanceOf(RuntimeException.class).hasMessage("ACTIVE 상태가 아닙니다.");
    }

    // 통합이 맞는듯...
    @Test
    public void testGetToken() {
        // Given
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(token1));

        // When
        TokenEntity foundToken = tokenService.getToken("token1");

        // Then
        assertThat(foundToken).isNotNull();
        assertThat(foundToken.getTokenValue()).isEqualTo("token1");
    }

    @DisplayName("없는 토큰으로 대기열 조회시 에러")
    @Test
    public void testGetToken2() {
        // Given
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.empty());

        // When && Then
        assertThatThrownBy(()->tokenService.getToken("token1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 토큰 입니다.");
    }

    // 통합이 맞는듯...
    @Test
    public void testGetWaitingNumber() {
        // Given
        TokenEntity latestActiveToken = TokenEntity.builder()
                .id(2L) // 최신 ACTIVE 토큰의 ID
                .status(TokenStatus.ACTIVE)
                .tokenValue("latestActiveToken")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        TokenEntity firstPeding = TokenEntity.builder()
                .id(3L) // 최신 ACTIVE 토큰의 ID
                .status(TokenStatus.PENDING)
                .tokenValue("firstPeding")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(firstPeding));
        when(tokenRepository.findLatestActiveToken()).thenReturn(Optional.of(latestActiveToken));

        // When
        int waitingNumber = tokenService.getWaitingNumber("firstPeding");

        // Then
        assertThat(waitingNumber).isEqualTo(firstPeding.getId()-latestActiveToken.getId()); // token1의 ID는 1이고, token2의 ID는 2이므로 대기번호는 1
    }

    // 통합이 맞는듯...
    @Test
    public void testExpireToken() {
        // Given
        when(tokenRepository.findByStatusIn(any())).thenReturn(List.of(token1));

        // When
        List<TokenEntity> expiredTokens = tokenService.expireToken(LocalDateTime.now().plusMinutes(15));

        // Then
        assertThat(expiredTokens).hasSize(1);
        assertThat(expiredTokens.get(0).getStatus()).isEqualTo(TokenStatus.EXPIRED);
    }

    // 통합이 맞는듯...
    @Test
    public void testActivateToken() {
        // Given
        when(tokenRepository.findByStatusIn(any())).thenReturn(List.of(token1));
        when(tokenRepository.findTokensToActivate(anyInt()))
                .thenReturn(List.of(newToken));

        // When
        List<TokenEntity> activatedTokens = tokenService.activateToken(LocalDateTime.now());

        // Then
        assertThat(activatedTokens).hasSize(1);
        assertThat(activatedTokens.get(0).getStatus()).isEqualTo(TokenStatus.ACTIVE);
    }


    // 통합이 맞는듯...
    @Test
    public void testUpdateTokenToReserved() {
        // Given
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(token1));

        // When
        Boolean result = tokenService.updateTokenToReserved("token1", LocalDateTime.now());

        // Then
        assertThat(result).isTrue();
        assertThat(token1.getStatus()).isEqualTo(TokenStatus.RESERVED);
    }


    // 통합이 맞는듯...
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

}//end
