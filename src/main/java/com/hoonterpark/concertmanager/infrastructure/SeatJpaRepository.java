package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {

    Optional<SeatEntity> findBySeatNumber(String seatNumber);

    List<SeatEntity> findByConcertScheduleId(Long scheduleId);

    @Query("select s from SeatEntity s where s.status = 'RESERVED'")
    List<SeatEntity> findReservedSeat();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatEntity s WHERE s.id = :id")
    Optional<SeatEntity> findByIdWithLock(@Param("id") Long id);

}
