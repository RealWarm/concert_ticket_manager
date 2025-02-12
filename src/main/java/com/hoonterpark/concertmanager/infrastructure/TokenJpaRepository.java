package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//public interface TokenJpaRepository extends JpaRepository<TokenEntity, Long> {
//
//    Optional<TokenEntity> findByTokenValue(String tokenValue);
//
//    @Query("select t from TokenEntity t where t.status = 'ACTIVE' order by t.createdAt DESC")
//    Optional<TokenEntity> findLatestActiveToken(PageRequest of);
//
//    @Query("SELECT t FROM TokenEntity t WHERE t.status IN :statuses")
//    List<TokenEntity> findByStatusIn(@Param("statuses") List<TokenStatus> statuses);
//
//    // PENDING 상태의 토큰 중에서 가장 오래된 N개의 토큰을 조회
//    @Query("SELECT t FROM TokenEntity t WHERE t.status = 'PENDING' ORDER BY t.expiredAt")
//    Page<TokenEntity> findTokensToActivate(Pageable pageable);
//
//}//end
