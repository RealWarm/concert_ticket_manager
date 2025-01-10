package com.hoonterpark.concertmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.enums.SeatStatus;
import com.hoonterpark.concertmanager.domain.repository.SeatRepository;
import com.hoonterpark.concertmanager.domain.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public class SeatServiceUnitTest {

    @InjectMocks
    private SeatService seatService;

    @Mock
    private SeatRepository seatRepository;

    private SeatEntity seat;
    private SeatEntity reservedSeat;
    private SeatEntity expiredSeat;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        seat = SeatEntity.builder()
                .concertScheduleId(1L)
                .seatNumber("A1")
                .status(SeatStatus.AVAILABLE)
                .seatPrice(10000L)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
        reservedSeat = SeatEntity.builder()
                .concertScheduleId(1L)
                .seatNumber("A2")
                .status(SeatStatus.RESERVED)
                .seatPrice(10000L)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
        expiredSeat = SeatEntity.builder()
                .concertScheduleId(1L)
                .seatNumber("A3")
                .status(SeatStatus.RESERVED)
                .seatPrice(10000L)
                .expiredAt(LocalDateTime.now().minusMinutes(10))
                .build();
    }

    @Test
    public void testMakeConcertReservationSeat() {
        // Given
        when(seatRepository.save(any(SeatEntity.class))).thenReturn(seat);

        // When
        SeatEntity newSeat = seatService.makeConcertReservationSeat(seat.getConcertScheduleId(), seat.getSeatPrice());

        // Then
        assertThat(newSeat).isNotNull();
        assertThat(newSeat.getConcertScheduleId()).isEqualTo(seat.getConcertScheduleId());
        assertThat(newSeat.getSeatPrice()).isEqualTo(seat.getSeatPrice());
        assertThat(newSeat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    public void testGetConcertSeats() {
        // Given
        when(seatRepository.findByConcertScheduleId(seat.getConcertScheduleId())).thenReturn(Arrays.asList(seat));

        // When
        var seats = seatService.getConcertSeats(seat.getConcertScheduleId());

        // Then
        assertThat(seats).isNotEmpty();
        assertThat(seats.get(0).getSeatNumber()).isEqualTo(seat.getSeatNumber());
    }

    @Test
    public void testReserveSeat() {
        // Given
        when(seatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(SeatEntity.class))).thenReturn(seat);

        LocalDateTime now = LocalDateTime.now();

        // When
        SeatEntity reservedSeat = seatService.reserveSeat(seat.getId(), now);

        // Then
        assertThat(reservedSeat.getStatus()).isEqualTo(SeatStatus.RESERVED);
        assertThat(reservedSeat.getExpiredAt()).isAfter(now);
    }

    @Test
    public void testReserveSeat_NotAvailable() {
        // Given

        when(seatRepository.findById(reservedSeat.getId())).thenReturn(Optional.of(reservedSeat));

        LocalDateTime now = LocalDateTime.now();

        // When & Then
        assertThatThrownBy(() -> seatService.reserveSeat(seat.getId(), now)).hasMessage("공석만 좌석 예약이 가능합니다.");

    }

    @Test
    public void testPayForSeat() {
        // Given
        when(seatRepository.findById(reservedSeat.getId())).thenReturn(Optional.of(reservedSeat));
        when(seatRepository.save(any(SeatEntity.class))).thenReturn(reservedSeat);

        LocalDateTime now = LocalDateTime.now();

        // When
        SeatEntity paidSeat = seatService.payForSeat(reservedSeat.getId(), now);

        // Then
        assertThat(paidSeat.getStatus()).isEqualTo(SeatStatus.PAID);
    }

    @Test
    public void testPayForSeat_NotReserved() {
        // Given
        when(seatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));

        LocalDateTime now = LocalDateTime.now();

        // When & Then
        assertThatThrownBy(()->seatService.payForSeat(seat.getId(), now)).hasMessage("예약된 좌석이 아닙니다."); // 예외 메시지 확인
    }

    @Test
    public void testReleaseSeat() {
        // Given
        when(seatRepository.findReservedSeat()).thenReturn(Arrays.asList(expiredSeat));
        when(seatRepository.save(any(SeatEntity.class))).thenReturn(expiredSeat);

        LocalDateTime now = LocalDateTime.now();

        // When
        var releasedSeats = seatService.releaseSeat(now);

        // Then
        assertThat(releasedSeats).isNotEmpty();
        assertThat(releasedSeats.get(0).getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }
}
