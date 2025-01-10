package com.hoonterpark.concertmanager.domain.repository;


import com.hoonterpark.concertmanager.domain.entity.ConcertEntity;
import com.hoonterpark.concertmanager.domain.enums.ConcertStatus;

import java.util.List;
import java.util.Optional;


public interface ConcertRepository {

    // 단일저장
    ConcertEntity save(ConcertEntity concert);

    // 복수저장
    List<ConcertEntity> saveAll(List<ConcertEntity> concerts);

    // id로 검색
    Optional<ConcertEntity> findById(Long id);

    // 이름검색
    Optional<ConcertEntity> findByConcertName(String concertName);

    // 예약가능한 콘서트 조회
    List<ConcertEntity> findByStatus(ConcertStatus status);

}
