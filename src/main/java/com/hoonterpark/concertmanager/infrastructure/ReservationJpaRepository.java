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

    List<ReservationEntity> findByUserId(Long userId);

    Optional<ReservationEntity> findByUserIdAndConcertScheduleId(Long userId, Long concertScheduleId);

    List<ReservationEntity> findByConcertScheduleId(Long concertScheduleId);

    List<ReservationEntity> findBySeatId(Long seatId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from ReservationEntity r where r.id = :id")
    Optional<ReservationEntity> findByIdWithLock(Long id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select r from ReservationEntity r where r.id = :id")
    Optional<ReservationEntity> findByIdWithOptimisticLock(Long id);

    Optional<ReservationEntity> findById(Long id);

}
