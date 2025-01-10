package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.enums.SeatStatus;
import com.hoonterpark.concertmanager.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public SeatEntity save(SeatEntity seat) {
        return seatJpaRepository.save(seat);
    }


    @Override
    public List<SeatEntity> saveAll(List<SeatEntity> seats) {
        return seatJpaRepository.saveAll(seats);
    }


    @Override
    public Optional<SeatEntity> findById(Long id) {
        return seatJpaRepository.findById(id);
    }


    @Override
    public Optional<SeatEntity> findBySeatNumber(String seatNumber) {
        return seatJpaRepository.findBySeatNumber(seatNumber);
    }


    @Override
    public List<SeatEntity> findByConcertScheduleId(Long scheduleId) {
        return seatJpaRepository.findByConcertScheduleId(scheduleId);
    }


    @Override
    public List<SeatEntity> findReservedSeat() {
        return seatJpaRepository.findReservedSeat();
    }


}
