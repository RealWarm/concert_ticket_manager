package com.hoonterpark.concertmanager.Token;

import com.hoonterpark.concertmanager.application.TokenFacade;
import com.hoonterpark.concertmanager.common.error.CustomException;
import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.entity.UserEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.presentation.controller.request.UserTokenRequest;
import com.hoonterpark.concertmanager.application.result.TokenResult;
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

public class TokenQueueFacadeIntegrationTest {
//
//    @InjectMocks
//    private TokenFacade tokenFacade;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private TokenService tokenService;
//
//    private UserTokenRequest userTokenRequest;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        userTokenRequest = new UserTokenRequest();
//        userTokenRequest.setUserId(1L);
//    }
//
//    @Test
//    public void testIssueToken() {
//        // Given
//        TokenEntity newToken = TokenEntity.builder()
//                .status(TokenStatus.PENDING)
//                .tokenValue("token-value")
//                .expiredAt(LocalDateTime.now().plusMinutes(10))
//                .build();
//
//        when(userService.findById(anyLong())).thenReturn(new UserEntity(1L, "hoon", 1000L)); // Mock user
//        when(tokenService.issueToken(any())).thenReturn(newToken);
//        when(tokenService.getWaitingNumber(anyString())).thenReturn(0L); // Mock waiting number
//
//        // When
//        TokenResult.TokenQueue response = tokenFacade.issueToken(userTokenRequest.getUserId(), LocalDateTime.now());
//
//        // Then
//        assertThat(response).isNotNull();
//        assertThat(response.token()).isEqualTo("token-value");
//        assertThat(response.queuePosition()).isEqualTo(0);
//    }
//
//    @Test
//    public void testGetQueueToken() {
//        // Given
//        TokenEntity existingToken = TokenEntity.builder()
//                .tokenValue("token-value")
//                .status(TokenStatus.ACTIVE)
//                .expiredAt(LocalDateTime.now().plusMinutes(10))
//                .build();
//
//        when(tokenService.getToken(anyString())).thenReturn(existingToken);
//        when(tokenService.getWaitingNumber(anyString())).thenReturn(1L); // Mock waiting number
//
//        // When
//        TokenResult.TokenQueue response = tokenFacade.getQueueToken("token-value");
//
//        // Then
//        assertThat(response).isNotNull();
//        assertThat(response.tokenStatus()).isEqualTo(TokenStatus.ACTIVE);
//        assertThat(response.queuePosition()).isEqualTo(1);
//    }
//
//    @Test
//    public void testGetQueueToken_NotFound() {
//        // When & Then
//        assertThatThrownBy(() -> tokenFacade.getQueueToken("invalid-token-value"))
//                .isInstanceOf(CustomException.class)
//                .hasMessage("Not Exist Token!");
//    }
//
//    @Test
//    public void testExpireToken() {
//        // When
//        tokenFacade.expireToken(LocalDateTime.now().plusMinutes(1));
//        // Then
//        verify(tokenService).expireToken(any());
//    }
//
//    @Test
//    public void testActivateToken() {
//        // Given
//        // No specific setup needed for this test
//
//        // When
//        tokenFacade.activateToken(LocalDateTime.now().plusMinutes(1));
//
//        // Then
//        verify(tokenService).activateToken(any());
//    }
}
