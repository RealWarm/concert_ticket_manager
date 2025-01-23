package com.hoonterpark.concertmanager.domain.repository;


import com.hoonterpark.concertmanager.domain.entity.SeatEntity;

import java.util.List;
import java.util.Optional;


public interface SeatRepository {

    // 단일저장
    SeatEntity save(SeatEntity seat);

    // 복수 저장
    List<SeatEntity> saveAll(List<SeatEntity> seats);

    // 좌석Id로 검색
    Optional<SeatEntity> findById(Long id);


    // 좌석명으로 검색
    Optional<SeatEntity> findBySeatNumber(String seatNumber);

    // 콘서트 스케줄Id로 찾기
    List<SeatEntity> findByConcertScheduleId(Long scheduleId);

    // RESERVED 상태의 좌석 조회하기(스케줄러)
    List<SeatEntity> findReservedSeat();


}//end
