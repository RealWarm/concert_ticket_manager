package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationJpaRepository;


    @Override
    public ReservationEntity save(ReservationEntity reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public List<ReservationEntity> saveAll(List<ReservationEntity> reservations) {
        return reservationJpaRepository.saveAll(reservations);
    }

    @Override
    public Optional<ReservationEntity> findById(Long id) {
        return reservationJpaRepository.findById(id);
    }

    @Override
    public List<ReservationEntity> findByUserId(Long userId) {
        return reservationJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<ReservationEntity> findByUserIdAndConcertScheduleId(Long userId, Long concertScheduleId) {
        return reservationJpaRepository.findByUserIdAndConcertScheduleId(userId, concertScheduleId);
    }

    @Override
    public List<ReservationEntity> findByConcertId(Long concertId) {
        return reservationJpaRepository.findByUserId(concertId);
    }

    @Override
    public List<ReservationEntity> findByIdConcertScheduleId(Long concertScheduleId) {
        return reservationJpaRepository.findByConcertScheduleId(concertScheduleId);
    }

    @Override
    public List<ReservationEntity> findBySeatId(Long seatId) {
        return reservationJpaRepository.findBySeatId(seatId);
    }


}
