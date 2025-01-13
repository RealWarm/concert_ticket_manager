package com.hoonterpark.concertmanager.application;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.service.ReservationService;
import com.hoonterpark.concertmanager.domain.service.SeatService;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Component
@Transactional
@RequiredArgsConstructor
public class ReservationUsecase {
    private final UserService userService;
    private final TokenService tokenService;
    private final SeatService seatService;
    private final ReservationService reservationService;


    // 콘서트 예약
    public ReservationResponse.Reservation reserveSeat(
            ReservationRequest request,
            LocalDateTime now
    ){
        // 유저 정보 확인
        userService.findById(request.getUserId());

        // 토큰검증
        TokenEntity active = tokenService.isActive(request.getToken(), now);

        // 토큰을 예약상태로 변경
        active.updateTokenToReserved(now);

        // 좌석예약하기
        SeatEntity seatEntity = seatService.reserveSeat(request.getSeatId(), now);

        // 예약 내역 만들기
        ReservationEntity reservation = reservationService.makeReservation(request, seatEntity.getSeatPrice(), now);

        return new ReservationResponse.Reservation(reservation.getId());
    }//reserveSeat


    // 콘서트 좌석 다시 활성화시키기(스케줄러)
    public void releaseSeat(LocalDateTime now){
        seatService.releaseSeat(now);
    }

}//end
