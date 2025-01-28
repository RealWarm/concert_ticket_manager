package com.hoonterpark.concertmanager.domain;

import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.enums.SeatStatus;
import com.hoonterpark.concertmanager.domain.repository.SeatRepository;
import com.hoonterpark.concertmanager.domain.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



@Transactional
@SpringBootTest
public class SeatServiceIntegrationTest {

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    private Long concertScheduleId;
    private Long seatPrice = 10000L;

    @BeforeEach
    public void setUp() {
        // 콘서트 스케줄 ID 설정 (테스트를 위해 임의의 값 사용)
        concertScheduleId = 1L;
    }

    @Test
    public void testMakeConcertReservationSeat() {
        // When
        SeatEntity newSeat = seatService.makeConcertReservationSeat(concertScheduleId, seatPrice);

        // Then
        assertThat(newSeat).isNotNull();
        assertThat(newSeat.getConcertScheduleId()).isEqualTo(concertScheduleId);
        assertThat(newSeat.getSeatPrice()).isEqualTo(seatPrice);
        assertThat(newSeat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    public void testGetConcertSeats() {
        // Given
        seatService.makeConcertReservationSeat(concertScheduleId, seatPrice); // 좌석 생성

        // When
        List<SeatEntity> seats = seatService.getConcertSeats(concertScheduleId);

        // Then
        assertThat(seats).isNotEmpty();
        assertThat(seats.get(0).getConcertScheduleId()).isEqualTo(concertScheduleId);
    }

    @Test
    public void testReserveSeat() {
        // Given
        SeatEntity seat = seatService.makeConcertReservationSeat(concertScheduleId, seatPrice); // 좌석 생성
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
        SeatEntity seat = seatService.makeConcertReservationSeat(concertScheduleId, seatPrice); // 좌석 생성
        LocalDateTime now = LocalDateTime.now();
        seat.reserveSeat(now); // 좌석 예약

        // When & Then
        assertThatThrownBy(() -> seatService.reserveSeat(seat.getId(), now))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("공석만 좌석 예약이 가능합니다."); // 예외 메시지 확인
    }

    @Test
    public void testPayForSeat() {
        // Given
        SeatEntity seat = seatService.makeConcertReservationSeat(concertScheduleId, seatPrice); // 좌석 생성
        LocalDateTime now = LocalDateTime.now();
        seatService.reserveSeat(seat.getId(), now); // 좌석 예약

        // When
        SeatEntity paidSeat = seatService.payForSeat(seat.getId(), now);

        // Then
        assertThat(paidSeat.getStatus()).isEqualTo(SeatStatus.PAID);
    }

    @Test
    public void testPayForSeat_NotReserved() {
        // Given
        SeatEntity seat = seatService.makeConcertReservationSeat(concertScheduleId, seatPrice); // 좌석 생성
        LocalDateTime now = LocalDateTime.now();

        // When & Then
        assertThatThrownBy(() -> seatService.payForSeat(seat.getId(), now))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("예약된 좌석이 아닙니다."); // 예외 메시지 확인
    }

    @Test
    public void testReleaseSeat() {
        // Given
        SeatEntity seat = seatService.makeConcertReservationSeat(concertScheduleId, seatPrice); // 좌석 생성
        LocalDateTime now = LocalDateTime.now();
        seatService.reserveSeat(seat.getId(), now); // 좌석 예약

        // Simulate expiration by waiting longer than the expiration time
        try {
            Thread.sleep(11000); // 11초 대기 (10초 만료)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        List<SeatEntity> releasedSeats = seatService.releaseSeat(now);

        // Then
        assertThat(releasedSeats).isNotEmpty();
        assertThat(releasedSeats.get(0).getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }
}
