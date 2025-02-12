package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;
    private final RedisTemplate<String, Object> redisTemplate;


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
        List<SeatEntity> seats = (List<SeatEntity>) redisTemplate.opsForValue().get("scheduleId:" + scheduleId);

        if (seats == null) {
            seats = seatJpaRepository.findByConcertScheduleId(scheduleId);
            if (seats != null) {
                log.info("cached!!!");
                redisTemplate.opsForValue().set("scheduleId:" + scheduleId, seats);
            }
        }
        return seats;
    }


    @Override
    public List<SeatEntity> findReservedSeat() {
        return seatJpaRepository.findReservedSeat();
    }

    @Override
    public Optional<SeatEntity> findByIdWithLock(Long id) {
        return seatJpaRepository.findByIdWithOptimisticLock(id);
    }

}
