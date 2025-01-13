package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.ConcertScheduleEntity;
import com.hoonterpark.concertmanager.domain.repository.ConcertScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class ConcertScheduleRepositoryImpl implements ConcertScheduleRepository {
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Override
    public ConcertScheduleEntity save(ConcertScheduleEntity concertScheduleEntity) {
        return concertScheduleJpaRepository.save(concertScheduleEntity);
    }

    @Override
    public List<ConcertScheduleEntity> saveAll(List<ConcertScheduleEntity> concertScheduleEntities) {
        return concertScheduleJpaRepository.saveAll(concertScheduleEntities);
    }

    @Override
    public Optional<ConcertScheduleEntity> findById(Long id) {
        return concertScheduleJpaRepository.findById(id);
    }

    @Override
    public List<ConcertScheduleEntity> findByConcertId(Long concertId) {
        return concertScheduleJpaRepository.findByConcertId(concertId);
    }

    @Override
    public List<ConcertScheduleEntity> findAvailableConcertSchedules(Long concertId, LocalDateTime now) {
        return concertScheduleJpaRepository.findUpcomingConcerts(concertId, now);
    }

}