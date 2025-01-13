package com.hoonterpark.concertmanager.domain.repository;

import com.hoonterpark.concertmanager.domain.entity.ConcertScheduleEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertScheduleRepository {

    // 단일저장
    ConcertScheduleEntity save (ConcertScheduleEntity concertScheduleEntity);

    // 복수 저장
    List<ConcertScheduleEntity> saveAll (List<ConcertScheduleEntity> concertScheduleEntities);

    // id로 검색
    Optional<ConcertScheduleEntity> findById(Long id);

    // concertId로 모든 스케줄 검색
    List<ConcertScheduleEntity> findByConcertId(Long concertId);

    // 예약 가능한 스케줄 조회
    List<ConcertScheduleEntity> findAvailableConcertSchedules(Long concertId, LocalDateTime now);


}
