package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByUserId(Long userId);

    Optional<ReservationEntity> findByUserIdAndConcertScheduleId(Long userId, Long concertScheduleId);

    List<ReservationEntity> findByConcertScheduleId(Long concertScheduleId);

    List<ReservationEntity> findBySeatId(Long seatId);
}
