package com.hoonterpark.concertmanager.application;

import com.hoonterpark.concertmanager.domain.entity.PaymentEntity;
import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.entity.UserEntity;
import com.hoonterpark.concertmanager.domain.service.*;
import com.hoonterpark.concertmanager.presentation.controller.request.PaymentRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class PaymentUseCase {
    private final UserService userService;
    private final SeatService seatService;
    private final TokenService tokenService;
    private final PaymentService paymentService;
    private final ReservationService reservationService;


    // 콘서트 결제
    public PaymentResponse makePayment(PaymentRequest request, LocalDateTime now){
        // 토큰검증 및 토큰상태변환
        tokenService.updateTokenToPaid(request.getToken(), now);

        // 예약내역 존재 확인 후 결제완료로 상태 변환
        ReservationEntity reservation = reservationService.payForReservation(request.getReservationId(), now);

        // 유저 잔액확인 후 잔액차감
        userService.payment(reservation.getUserId(), reservation.getTotalPrice());

        // 좌석 확인 후 결제완료로 상태 변환
        seatService.payForSeat(reservation.getSeatId(), now);

        // 결제내역 생성
        PaymentEntity payment = paymentService.makePayment(reservation.getId(), reservation.getTotalPrice());

        return new PaymentResponse(payment.getId());
    }//makePayment

}
