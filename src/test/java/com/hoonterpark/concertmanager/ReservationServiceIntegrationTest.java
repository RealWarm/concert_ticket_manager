package com.hoonterpark.concertmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.enums.ReservationStatus;
import com.hoonterpark.concertmanager.domain.repository.ReservationRepository;
import com.hoonterpark.concertmanager.domain.service.ReservationService;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // H2 데이터베이스 사용
@Transactional // 각 테스트 후 롤백
public class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    private ReservationRequest reservationRequest;


    @BeforeEach
    public void setUp() {
        // 테스트 데이터 준비
        reservationRequest = ReservationRequest.builder()
                .userId(1L)
                .concertScheduleId(1L)
                .seatId(1L)
                .build();
    }


    @Test
    public void testMakeReservation() {
        // Given
        Long seatPrice = 10000L;
        LocalDateTime now = LocalDateTime.now();

        // When
        ReservationEntity reservation = reservationService.makeReservation(reservationRequest, seatPrice, now);

        // Then
        assertThat(reservation).isNotNull();
        assertThat(reservation.getUserId()).isEqualTo(1L);
        assertThat(reservation.getConcertScheduleId()).isEqualTo(1L);
        assertThat(reservation.getSeatId()).isEqualTo(1L);
        assertThat(reservation.getTotalPrice()).isEqualTo(seatPrice);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }


    @Test
    public void testMakeReservation_AlreadyExists() {
        // Given
        Long seatPrice = 10000L;
        LocalDateTime now = LocalDateTime.now();
        reservationService.makeReservation(reservationRequest, seatPrice, now); // 첫 번째 예약

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reservationService.makeReservation(reservationRequest, seatPrice, now); // 두 번째 예약
        });

        assertThat(exception.getMessage()).isEqualTo("예약 내역이 이미 있습니다.");
    }

    @Test
    public void testPayForReservation() {
        // Given
        Long seatPrice = 10000L;
        LocalDateTime now = LocalDateTime.now();
        ReservationEntity reservation = reservationService.makeReservation(reservationRequest, seatPrice, now);

        // When
        ReservationEntity paidReservation = reservationService.payForReservation(reservation.getId(), now);

        // Then
        assertThat(paidReservation).isNotNull();
        assertThat(paidReservation.getStatus()).isEqualTo(ReservationStatus.PAID);
    }


    @Test
    public void testPayForReservation_NotFound() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.payForReservation(999L, LocalDateTime.now()); // 존재하지 않는 예약 ID
        });

        assertThat(exception.getMessage()).isEqualTo("예약 내역이 없습니다.");
    }


}
