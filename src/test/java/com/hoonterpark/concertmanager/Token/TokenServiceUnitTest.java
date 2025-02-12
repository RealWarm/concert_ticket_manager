package com.hoonterpark.concertmanager.Token;

import com.hoonterpark.concertmanager.common.error.CustomException;
import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.repository.TokenRepository;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.infrastructure.TokenJpaRepository;
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




//public class TokenServiceUnitTest {
//
//    @InjectMocks
//    private TokenService tokenService;
//
//    @Mock
//    private TokenRepository tokenRepository;
//
//    @Mock
//    private TokenJpaRepository tokenJpaRepository;
//
//    private TokenEntity token1;
//    private TokenEntity newToken;
//
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        tokenJpaRepository.deleteAllInBatch();
//
//        token1 = TokenEntity.builder()
//                .id(1L)
//                .status(TokenStatus.ACTIVE)
//                .tokenValue("token1")
//                .expiredAt(LocalDateTime.now().plusMinutes(10))
//                .build();
//
//        newToken = TokenEntity.builder()
//                .id(10L)
//                .status(TokenStatus.PENDING)
//                .tokenValue("newtoken1")
//                .expiredAt(LocalDateTime.now().plusMinutes(10))
//                .build();
//
//    }
//
//    @DisplayName("없는 토큰으로 대기열 조회시 에러")
//    @Test
//    public void testGetToken2() {
//        // Given
//        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.empty());
//
//        // When && Then
//        assertThatThrownBy(()->tokenService.getToken("token1"))
//                .isInstanceOf(CustomException.class)
//                .hasMessage("Not Exist Token!");
//    }
//
//    @DisplayName("Pending 상태의 토큰을 활성화인지 확인 조회하면 에러발생")
//    @Test
//    public void testIfNotActiveThenRuntimeExceptionInvoked(){
//        TokenEntity PendingToken = TokenEntity.builder()
//                                                    .id(3L)
//                                                    .status(TokenStatus.PENDING)
//                                                    .tokenValue("PendingToken")
//                                                    .expiredAt(LocalDateTime.now().minusMinutes(10))
//                                                .build();
//        // Given
//        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(PendingToken));
//
//        // When && Then
//        assertThatThrownBy(()->tokenService.isActive("PendingToken", LocalDateTime.now()))
//                .isInstanceOf(RuntimeException.class).hasMessage("ACTIVE 상태가 아닙니다.");
//    }
//
//
//    @DisplayName("Active 상태지만 유효시간이 지난 토큰을 활성체크하면 에러")
//    @Test
//    public void testIsActive2() {
//        TokenEntity activeButTimeOver = TokenEntity.builder()
//                                                        .id(3L)
//                                                        .status(TokenStatus.ACTIVE)
//                                                        .tokenValue("activeButTimeOver")
//                                                        .expiredAt(LocalDateTime.now().minusMinutes(10))
//                                                    .build();
//        // Given
//        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(activeButTimeOver));
//
//        // When && Then
//        assertThatThrownBy(()->tokenService.isActive("activeButTimeOver", LocalDateTime.now()))
//                .isInstanceOf(RuntimeException.class).hasMessage("예약가능한 유효시간이 지났습니다.");
//    }
//
//
//    @DisplayName("만료된 상태의 토큰을 활성화 체크하면 에러")
//    @Test
//    public void testIsActive3() {
//        // Given
//        TokenEntity token2 = TokenEntity.builder()
//                                            .id(2L)
//                                            .status(TokenStatus.EXPIRED)
//                                            .tokenValue("token2")
//                                            .expiredAt(LocalDateTime.now().minusMinutes(10))
//                                        .build();
//
//        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(token2));
//
//        // When && Then
//        assertThatThrownBy(()->tokenService.isActive("token2", LocalDateTime.now()))
//                .isInstanceOf(RuntimeException.class).hasMessage("ACTIVE 상태가 아닙니다.");
//    }
//
//
//    // 통합이 맞는듯...
//    @Test
//    public void testExpireToken() {
//        // Given
//        when(tokenRepository.findByStatusIn(any())).thenReturn(List.of(token1));
//
//        // When
//        List<TokenEntity> expiredTokens = tokenService.expireToken(LocalDateTime.now().plusMinutes(15));
//
//        // Then
//        assertThat(expiredTokens).hasSize(1);
//        assertThat(expiredTokens.get(0).getStatus()).isEqualTo(TokenStatus.EXPIRED);
//    }
//
//
//    // 통합이 맞는듯...
//    @Test
//    public void testActivateToken() {
//        // Given
//        when(tokenRepository.findByStatusIn(any())).thenReturn(List.of(token1));
//        when(tokenRepository.findTokensToActivate(anyInt()))
//                .thenReturn(List.of(newToken));
//
//        // When
//        List<TokenEntity> activatedTokens = tokenService.activateToken(LocalDateTime.now());
//
//        // Then
//        assertThat(activatedTokens).hasSize(1);
//        assertThat(activatedTokens.get(0).getStatus()).isEqualTo(TokenStatus.ACTIVE);
//    }
//
//
//    // 통합이 맞는듯...
//    @Test
//    public void testUpdateTokenToReserved() {
//        // Given
//        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(token1));
//
////        // When
////        Boolean result = tokenService.updateTokenToReserved("token1", LocalDateTime.now());
////
////        // Then
////        assertThat(result).isTrue();
////        assertThat(token1.getStatus()).isEqualTo(TokenStatus.RESERVED);
//    }
//
//
//    // 통합이 맞는듯...
//    @Test
//    public void testUpdateTokenToPaid() {
//        // Given
//        TokenEntity testUpdateToken = TokenEntity.builder()
//                .status(TokenStatus.RESERVED)
//                .tokenValue("testUpdateToken11")
//                .expiredAt(LocalDateTime.now().plusMinutes(10))
//                .build();
//        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.of(testUpdateToken));
//
//        // When
//        Boolean result = tokenService.updateTokenToPaid("token1", LocalDateTime.now());
//
//        // Then
//        assertThat(result).isTrue();
//        assertThat(testUpdateToken.getStatus()).isEqualTo(TokenStatus.PAID);
//    }
//
//}//end
