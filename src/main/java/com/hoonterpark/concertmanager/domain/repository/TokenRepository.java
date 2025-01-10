package com.hoonterpark.concertmanager.domain.repository;

import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {

    TokenEntity save(TokenEntity newToken);

    List<TokenEntity> saveAll(List<TokenEntity> newTokens);

    Optional<TokenEntity> findByTokenValue(String tokenValue);

    Optional<TokenEntity> findLatestActiveToken();

    List<TokenEntity> findByStatusIn(List<TokenStatus> statuses);

    // 구현에서 페이저블 쓰기
    // findTopLIMIT_ACTIVATE_USERByTokenStatusOrderByExpiredAtAsc(TokenStatus.PENDING, pageable);
    // Pageable pageable = PageRequest.of(0, N);
    List<TokenEntity> findTopNByTokenStatusOrderByExpiredAtAsc(List<TokenStatus> pending, int Limit);

}
