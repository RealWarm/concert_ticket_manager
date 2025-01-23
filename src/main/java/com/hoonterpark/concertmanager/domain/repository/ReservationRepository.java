package com.hoonterpark.concertmanager.domain.repository;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    ReservationEntity save(ReservationEntity reservation);

    List<ReservationEntity> saveAll(List<ReservationEntity> reservations);

    Optional<ReservationEntity> findById(Long id);

    List<ReservationEntity> findByUserId(Long userId);

    Optional<ReservationEntity> findByUserIdAndConcertScheduleId(Long userId, Long concertScheduleId);

    List<ReservationEntity> findByConcertId(Long concertId);

    List<ReservationEntity> findByIdConcertScheduleId(Long concertScheduleId);

    List<ReservationEntity> findBySeatId(Long seatId);

    Optional<ReservationEntity> findByIdWithLock(Long reservationId);

}
