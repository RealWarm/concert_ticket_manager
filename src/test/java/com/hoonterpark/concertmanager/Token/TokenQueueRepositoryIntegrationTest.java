package com.hoonterpark.concertmanager.Token;


import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class TokenQueueRepositoryIntegrationTest {

    @Autowired
    private TokenRepository tokenRepository;

    private TokenEntity token1;
    private TokenEntity token2;
    private TokenEntity token3;

    @BeforeEach
    public void setUp() {
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
                .status(TokenStatus.PENDING)
                .tokenValue("token3")
                .expiredAt(LocalDateTime.now().plusMinutes(20))
                .build();

        tokenRepository.saveAll(List.of(token1, token2, token3));
    }//setUp


    @Test
    public void testFindByTokenValue() {
        // 토큰 값으로 조회
        Optional<TokenEntity> foundToken = tokenRepository.findByTokenValue("token1");

        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getTokenValue()).isEqualTo("token1");
    }

    @Test
    public void testFindLatestActiveToken() {
        // 최신 ACTIVE 토큰 조회
        Optional<TokenEntity> latestActiveToken = tokenRepository.findLatestActiveToken();

        assertThat(latestActiveToken).isPresent();
        assertThat(latestActiveToken.get().getStatus()).isEqualTo(TokenStatus.ACTIVE);
    }

    @Test
    public void testFindByStatusIn() {
        // 여러 상태로 조회
        List<TokenEntity> foundTokens
                = tokenRepository.findByStatusIn(List.of(TokenStatus.ACTIVE, TokenStatus.PENDING));

        assertThat(foundTokens)
                .hasSize(3)
                .extracting("tokenValue")
                .containsExactlyInAnyOrder("token1","token2","token3");
    }

    @Test
    public void testFindTopNByTokenStatusOrderByExpiredAtAsc() {
        // PENDING 상태의 토큰 중 가장 오래된 N개 조회
        List<TokenEntity> foundTokens
                = tokenRepository.findTokensToActivate(1);

        assertThat(foundTokens).hasSize(1)
                .extracting("tokenValue")
                .containsExactlyInAnyOrder("token2");
    }

}