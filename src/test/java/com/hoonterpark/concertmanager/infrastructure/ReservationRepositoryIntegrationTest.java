package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.enums.ReservationStatus;
import com.hoonterpark.concertmanager.domain.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
public class ReservationRepositoryIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    private ReservationEntity reservation;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터 준비
        reservation = ReservationEntity.builder()
                .userId(1L)
                .concertScheduleId(1L)
                .seatId(1L)
                .totalPrice(10000L)
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .status(ReservationStatus.RESERVED)
                .build();

        reservationRepository.save(reservation);
    }

    @Test
    public void testSaveReservation() {
        // Given
        ReservationEntity newReservation = ReservationEntity.builder()
                .userId(2L)
                .concertScheduleId(2L)
                .seatId(2L)
                .totalPrice(20000L)
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .status(ReservationStatus.RESERVED)
                .build();

        // When
        ReservationEntity savedReservation = reservationRepository.save(newReservation);

        // Then
        assertThat(savedReservation).isNotNull();
        assertThat(savedReservation.getId()).isNotNull();
        assertThat(savedReservation.getUserId()).isEqualTo(2L);
    }


    @Test
    public void testFindById() {
        // When
        ReservationEntity foundReservation = reservationRepository.findById(reservation.getId()).orElseThrow();

        // Then
        assertThat(foundReservation).isNotNull();
        assertThat(foundReservation.getUserId()).isEqualTo(1L);
    }


    @Test
    public void testFindByUserId() {
        // When
        List<ReservationEntity> foundReservations = reservationRepository.findByUserId(1L);

        // Then
        assertThat(foundReservations).isNotEmpty();
        assertThat(foundReservations.size()).isGreaterThan(0);
    }


    @Test
    public void testFindByUserIdAndConcertScheduleId() {
        // When
        ReservationEntity foundReservation = reservationRepository.findByUserIdAndConcertScheduleId(1L, 1L).orElseThrow();

        // Then
        assertThat(foundReservation).isNotNull();
        assertThat(foundReservation.getConcertScheduleId()).isEqualTo(1L);
    }

    @Test
    public void testFindByConcertId() {
        // When
        List<ReservationEntity> foundReservations = reservationRepository.findByConcertId(1L);

        // Then
        assertThat(foundReservations).isNotEmpty();
    }


    @Test
    public void testFindByIdConcertScheduleId() {
        // When
        List<ReservationEntity> foundReservations = reservationRepository.findByIdConcertScheduleId(1L);

        // Then
        assertThat(foundReservations).isNotEmpty();
    }


    @Test
    public void testFindBySeatId() {
        // When
        List<ReservationEntity> foundReservations = reservationRepository.findBySeatId(1L);

        // Then
        assertThat(foundReservations).isNotEmpty();
    }

}
