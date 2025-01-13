package com.hoonterpark.concertmanager.application;

import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.entity.UserEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.presentation.controller.request.UserTokenRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TokenUseCaseIntegrationTest {

    @InjectMocks
    private TokenUseCase tokenUseCase;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    private UserTokenRequest userTokenRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userTokenRequest = new UserTokenRequest();
        userTokenRequest.setUserId(1L);
        userTokenRequest.setNow(LocalDateTime.now());
    }

    @Test
    public void testIssueToken() {
        // Given
        TokenEntity newToken = TokenEntity.builder()
                .status(TokenStatus.PENDING)
                .tokenValue("token-value")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(userService.findById(anyLong())).thenReturn(new UserEntity(1L, "hoon", 1000L)); // Mock user
        when(tokenService.makeToken(any())).thenReturn(newToken);
        when(tokenService.getWaitingNumber(anyString())).thenReturn(0); // Mock waiting number

        // When
        TokenResponse.TokenQueueResponse response = tokenUseCase.issueToken(userTokenRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("token-value");
        assertThat(response.queuePosition()).isEqualTo(0);
    }

    @Test
    public void testGetQueueToken() {
        // Given
        TokenEntity existingToken = TokenEntity.builder()
                .tokenValue("token-value")
                .status(TokenStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(tokenService.getToken(anyString())).thenReturn(existingToken);
        when(tokenService.getWaitingNumber(anyString())).thenReturn(1); // Mock waiting number

        // When
        TokenResponse.TokenQueueResponse response = tokenUseCase.getQueueToken("token-value");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("token-value");
        assertThat(response.queuePosition()).isEqualTo(1);
    }

    @Test
    public void testGetQueueToken_NotFound() {
        // When & Then
        assertThatThrownBy(() -> tokenUseCase.getQueueToken("invalid-token-value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 토큰 입니다.");
    }

    @Test
    public void testExpireToken() {
        // When
        tokenUseCase.expireToken(LocalDateTime.now().plusMinutes(1));
        // Then
        verify(tokenService).expireToken(any());
    }

    @Test
    public void testActivateToken() {
        // Given
        // No specific setup needed for this test

        // When
        tokenUseCase.activateToken(LocalDateTime.now().plusMinutes(1));

        // Then
        verify(tokenService).activateToken(any());
    }
}
