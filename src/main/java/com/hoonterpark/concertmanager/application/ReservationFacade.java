package com.hoonterpark.concertmanager.application;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.service.ReservationService;
import com.hoonterpark.concertmanager.domain.service.SeatService;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.interfaces.controller.api.request.ReservationRequest;
import com.hoonterpark.concertmanager.application.result.ReservationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationFacade {
    private final UserService userService;
    private final TokenService tokenService;
    private final SeatService seatService;
    private final ReservationService reservationService;


    // 콘서트 예약
    public ReservationResult.Reservation reserveSeat(
            ReservationRequest request,
            String token,
            LocalDateTime now
    ){
        log.info("reserveSeat invoked!!! {} ", request.toString());
        // 유저 정보 확인
        userService.findById(request.getUserId());

        // 좌석예약하기
        SeatEntity seatEntity = seatService.reserveSeat(request.getSeatId(), now);

        // 예약 내역 만들기
        ReservationEntity reservation = reservationService.makeReservation(request, seatEntity.getSeatPrice(), now);

        // 토큰을 예약상태로 변경
        tokenService.updateTokenToReserved(token, now);

        return new ReservationResult.Reservation(reservation.getId());
    }


    // 콘서트 좌석 다시 활성화시키기(스케줄러)
    public void releaseSeat(LocalDateTime now){
        seatService.releaseSeat(now);
    }

}//end
