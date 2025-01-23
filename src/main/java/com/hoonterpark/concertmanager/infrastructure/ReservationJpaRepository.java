package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

    Optional<ReservationEntity> findById(Long id);

    List<ReservationEntity> findByUserId(Long userId);

    Optional<ReservationEntity> findByUserIdAndConcertScheduleId(Long userId, Long concertScheduleId);

    List<ReservationEntity> findByConcertScheduleId(Long concertScheduleId);

    List<ReservationEntity> findBySeatId(Long seatId);


}
