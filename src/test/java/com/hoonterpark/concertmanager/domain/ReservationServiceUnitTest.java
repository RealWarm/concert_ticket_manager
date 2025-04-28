package com.hoonterpark.concertmanager.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.enums.ReservationStatus;
import com.hoonterpark.concertmanager.domain.repository.ReservationRepository;
import com.hoonterpark.concertmanager.domain.service.ReservationService;
import com.hoonterpark.concertmanager.interfaces.controller.api.request.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

public class ReservationServiceUnitTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    private ReservationRequest reservationRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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

        // Mocking the repository behavior
        when(reservationRepository.findByUserIdAndConcertScheduleId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

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

        // Mocking the repository behavior
        when(reservationRepository.findByUserIdAndConcertScheduleId(anyLong(), anyLong()))
                .thenReturn(Optional.of(new ReservationEntity(1L, 1L, 1L, 1000L, LocalDateTime.now().plusMinutes(10), ReservationStatus.RESERVED))); // 이미 예약이 존재하는 경우

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reservationService.makeReservation(reservationRequest, seatPrice, now); // 예약 시도
        });

        assertThat(exception.getMessage()).isEqualTo("예약 내역이 이미 있습니다.");
    }


    @Test
    public void testPayForReservation() {
        // Given
        Long seatPrice = 10000L;
        LocalDateTime now = LocalDateTime.now();
        ReservationEntity reservation = ReservationEntity.builder()
                .userId(1L)
                .concertScheduleId(1L)
                .seatId(1L)
                .totalPrice(seatPrice)
                .expiredAt(now.plusMinutes(10)) // 유효 시간 설정
                .status(ReservationStatus.RESERVED) // 예약 상태
                .build();

        // Mocking the repository behavior
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        // When
        ReservationEntity paidReservation = reservationService.payForReservation(reservation.getId(), now);

        // Then
        assertThat(paidReservation).isNotNull();
        assertThat(paidReservation.getStatus()).isEqualTo(ReservationStatus.PAID);
    }


    @Test
    public void testPayForReservation_Expired() {
        // Given
        Long seatPrice = 10000L;
        LocalDateTime now = LocalDateTime.now();
        ReservationEntity reservation = ReservationEntity.builder()
                .userId(1L)
                .concertScheduleId(1L)
                .seatId(1L)
                .totalPrice(seatPrice)
                .expiredAt(now.minusMinutes(10)) // 유효 시간이 만료된 상태
                .status(ReservationStatus.RESERVED) // 예약 상태
                .build();

        // Mocking the repository behavior
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        // When & Then
        assertThatThrownBy(() -> reservationService.payForReservation(reservation.getId(), now))
                .hasMessage("예약가능 시간이 지났습니다.");

    }


    @Test
    public void testPayForReservation_NotFound() {
        // Mocking the repository behavior
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.payForReservation(999L, LocalDateTime.now()); // 존재하지 않는 예약 ID
        });

        assertThat(exception.getMessage()).isEqualTo("예약 내역이 없습니다.");
    }

}
