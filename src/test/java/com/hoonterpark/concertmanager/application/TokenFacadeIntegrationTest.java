package com.hoonterpark.concertmanager.application;

import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.presentation.controller.request.UserTokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class TokenFacadeIntegrationTest {

    @InjectMocks
    private TokenFacade tokenFacade;

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
    }

    @Test
    public void testIssueToken() {
        // Given
        TokenEntity newToken = TokenEntity.builder()
                .status(TokenStatus.PENDING)
                .tokenValue("token-value")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

    }

    @Test
    public void testGetQueueToken() {
        // Given
        TokenEntity existingToken = TokenEntity.builder()
                .tokenValue("token-value")
                .status(TokenStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

    }

    @Test
    public void testGetQueueToken_NotFound() {
        // When & Then
        assertThatThrownBy(() -> tokenFacade.getQueueToken("invalid-token-value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 토큰 입니다.");
    }

    @Test
    public void testExpireToken() {
        // When
        tokenFacade.expireToken(LocalDateTime.now().plusMinutes(1));
        // Then
        verify(tokenService).expireToken(any());
    }

    @Test
    public void testActivateToken() {
        // Given
        // No specific setup needed for this test

        // When
        tokenFacade.activateToken(LocalDateTime.now().plusMinutes(1));

        // Then
        verify(tokenService).activateToken(any());
    }
}
