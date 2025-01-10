package com.hoonterpark.concertmanager.domain.service;


import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.enums.ReservationStatus;
import com.hoonterpark.concertmanager.domain.repository.ReservationRepository;
import com.hoonterpark.concertmanager.presentation.controller.request.PaymentRequest;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;



@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;


    // 예약한다.
    public ReservationEntity makeReservation(
            ReservationRequest request,
            Long seatPrice,
            LocalDateTime now
    ){
        Long userId = request.getUserId();
        Long seatId = request.getSeatId();
        Long concertScheduleId = request.getConcertScheduleId();
        Optional<ReservationEntity> reservation = reservationRepository
                                                    .findByUserIdAndConcertScheduleId(userId, concertScheduleId);
        if(reservation.isPresent()){
            throw new RuntimeException("예약 내역이 이미 있습니다.");
        }//if

        ReservationEntity newReservation = ReservationEntity.builder()
                .userId(userId)
                .concertScheduleId(concertScheduleId)
                .seatId(seatId)
                .totalPrice(seatPrice)
                .expiredAt(now.plusMinutes(10))
                .status(ReservationStatus.RESERVED)
                .build();

        return reservationRepository.save(newReservation);
    }//makeReservation


    // 결제한다.
    public ReservationEntity payForReservation(Long reservationId, LocalDateTime now){
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 내역이 없습니다."));

        return reservationRepository.save(reservation.payForReservation(now));
    }//payForReservation


}
