package com.hoonterpark.concertmanager.domain.service;


import com.hoonterpark.concertmanager.common.error.CustomException;
import com.hoonterpark.concertmanager.common.error.ErrorCode;
import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.repository.ReservationRepository;
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
    ) {
        Long userId = request.getUserId();
        Long seatId = request.getSeatId();
        Long concertScheduleId = request.getConcertScheduleId();

        Optional<ReservationEntity> reservation = reservationRepository
                .findByUserIdAndConcertScheduleId(userId, concertScheduleId);

        if (reservation.isPresent()) {
            throw new RuntimeException("예약 내역이 이미 있습니다.");
        }//if

        ReservationEntity newReservation = ReservationEntity.create(userId, concertScheduleId, seatId, seatPrice, now);

        return reservationRepository.save(newReservation);
    }//makeReservation


    // 결제한다.
    public ReservationEntity payForReservation(Long reservationId, LocalDateTime now) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "예약이 존재하지 않습니다."));

        return reservationRepository.save(reservation.payForReservation(now));
    }//payForReservation


}
