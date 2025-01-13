package com.hoonterpark.concertmanager.domain.service;


import com.hoonterpark.concertmanager.domain.entity.ConcertEntity;
import com.hoonterpark.concertmanager.domain.entity.ConcertScheduleEntity;
import com.hoonterpark.concertmanager.domain.enums.ConcertStatus;
import com.hoonterpark.concertmanager.domain.repository.ConcertRepository;
import com.hoonterpark.concertmanager.domain.repository.ConcertScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository concertScheduleRepository;

    // 특정 콘서트
    public ConcertScheduleEntity findConcertScheduleById(Long id){
        return concertScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 스케줄 입니다."));
    }

    // 예약 가능한 콘서트 조회
    @Transactional(readOnly = true)
    public List<ConcertEntity> findAvailableConcerts() {
        List<ConcertEntity> allConcerts = concertRepository.findByStatus(ConcertStatus.AVAILABLE);
        return allConcerts.stream()
                .filter(ConcertEntity::isAvailable)
                .collect(Collectors.toList());
    }

    // 예약 가능한 콘서트 날짜 조회
    @Transactional(readOnly = true)
    public List<ConcertScheduleEntity> findAvailableConcertSchedules(Long concertId, LocalDateTime now) {
        List<ConcertScheduleEntity> schedules = concertScheduleRepository.findAvailableConcertSchedules(concertId, now);
        return schedules.stream()
                .filter(schedule -> schedule.isAvailable(now))
                .collect(Collectors.toList());
    }


}//end
