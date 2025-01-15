package com.hoonterpark.concertmanager.application;


import com.hoonterpark.concertmanager.domain.entity.ConcertEntity;
import com.hoonterpark.concertmanager.domain.entity.ConcertScheduleEntity;
import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.service.ConcertService;
import com.hoonterpark.concertmanager.domain.service.SeatService;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.presentation.controller.response.ConcertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ConcertUseCase {
    private final TokenService tokenService;
    private final ConcertService concertService;
    private final SeatService seatService;

    // 콘서트 목록 조회
    public List<ConcertResponse.Concert> getConcert(){
        List<ConcertEntity> availableConcerts = concertService.findAvailableConcerts();
        return availableConcerts.stream()
                .map(concert -> new ConcertResponse.Concert(concert.getConcertName()))
                .collect(Collectors.toList());
    }

    // 콘서트 날짜 조회
    public List<ConcertResponse.ConcertDate> getConcertDate(Long concertId, LocalDateTime now) {
        List<ConcertScheduleEntity> availableConcertSchedules
                = concertService.findAvailableConcertSchedules(concertId, now);
        return availableConcertSchedules.stream()
                .map(cs -> new ConcertResponse.ConcertDate(cs.getPerformanceDay()))
                .collect(Collectors.toList());
    }


    // 콘서트 좌석 조회
    public List<ConcertResponse.ConcertSeat> getConcertSeat(Long concertScheduleId, String tokenValue, LocalDateTime now){
        // 토큰 검증
        tokenService.isActive(tokenValue, now);

        // 콘서트 스케줄이 존재하니?
        concertService.findConcertScheduleById(concertScheduleId);

        // 콘서트 스케줄에 해당하는 모든 좌석 조회
        List<SeatEntity> concertSeats = seatService.getConcertSeats(concertScheduleId);

        return concertSeats.stream()
                .map(cs -> new ConcertResponse.ConcertSeat(cs.getId(), cs.getSeatNumber(), cs.getStatus(), cs.getSeatPrice()))
                .collect(Collectors.toList());
    }

}//end
