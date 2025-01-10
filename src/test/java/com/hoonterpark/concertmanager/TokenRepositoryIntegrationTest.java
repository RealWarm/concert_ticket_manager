package com.hoonterpark.concertmanager;


import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.repository.TokenRepository;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class TokenRepositoryIntegrationTest {

    @Autowired
    private TokenRepository tokenRepository;

    private TokenEntity token1;
    private TokenEntity token2;

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

        tokenRepository.save(token1);
        tokenRepository.save(token2);
    }

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

        assertThat(foundTokens).hasSize(2);
    }

    @Test
    public void testFindTopNByTokenStatusOrderByExpiredAtAsc() {
        // PENDING 상태의 토큰 중 가장 오래된 N개 조회
        List<TokenEntity> foundTokens
                = tokenRepository.findTopNByTokenStatusOrderByExpiredAtAsc(List.of(TokenStatus.PENDING), 1);

        assertThat(foundTokens).hasSize(1);
        assertThat(foundTokens.get(0).getTokenValue()).isEqualTo("token2");
    }
}