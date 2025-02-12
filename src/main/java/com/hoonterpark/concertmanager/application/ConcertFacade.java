package com.hoonterpark.concertmanager.application;


import com.hoonterpark.concertmanager.domain.entity.ConcertEntity;
import com.hoonterpark.concertmanager.domain.entity.ConcertScheduleEntity;
import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.service.ConcertService;
import com.hoonterpark.concertmanager.domain.service.SeatService;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.presentation.controller.response.ConcertResult;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ConcertFacade {
    private final TokenService tokenService;
    private final ConcertService concertService;
    private final SeatService seatService;

    // 콘서트 목록 조회
    @Cacheable(value = "concerts", cacheManager = "cacheManager")
    public List<ConcertResult.Concert> getConcert(){
        List<ConcertEntity> availableConcerts = concertService.findAvailableConcerts();
        return availableConcerts.stream()
                .map(concert -> new ConcertResult.Concert(concert.getConcertName()))
                .collect(Collectors.toList());
    }

    // 콘서트 날짜 조회
    @Cacheable(key = "#concertId", value = "schedules", cacheManager = "cacheManager")
    public List<ConcertResult.ConcertDate> getConcertDate(Long concertId, LocalDateTime now) {
        List<ConcertScheduleEntity> availableConcertSchedules
                = concertService.findAvailableConcertSchedules(concertId, now);
        return availableConcertSchedules.stream()
                .map(cs -> new ConcertResult.ConcertDate(cs.getPerformanceDay()))
                .collect(Collectors.toList());
    }


    // 콘서트 좌석 조회
    @Cacheable(key = "#concertScheduleId", value = "seats", cacheManager = "cacheManager")
    public List<ConcertResult.ConcertSeat> getConcertSeat(Long concertScheduleId, String tokenValue, LocalDateTime now){
        // 토큰 검증
        tokenService.isActive(tokenValue);

        // 콘서트 스케줄이 존재하니?
        concertService.findConcertScheduleById(concertScheduleId);

        // 콘서트 스케줄에 해당하는 모든 좌석 조회
        List<SeatEntity> concertSeats = seatService.getConcertSeats(concertScheduleId);

        return concertSeats.stream()
                .map(cs -> new ConcertResult.ConcertSeat(cs.getId(), cs.getSeatNumber(), cs.getStatus(), cs.getSeatPrice()))
                .collect(Collectors.toList());
    }

}//end
