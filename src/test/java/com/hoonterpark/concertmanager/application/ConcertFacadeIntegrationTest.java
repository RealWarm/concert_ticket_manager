package com.hoonterpark.concertmanager.application;

import com.hoonterpark.concertmanager.domain.entity.ConcertEntity;
import com.hoonterpark.concertmanager.domain.entity.ConcertScheduleEntity;
import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.enums.ConcertStatus;
import com.hoonterpark.concertmanager.domain.enums.SeatStatus;
import com.hoonterpark.concertmanager.domain.repository.*;
import com.hoonterpark.concertmanager.domain.service.ConcertService;
import com.hoonterpark.concertmanager.domain.service.SeatService;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.presentation.controller.response.ConcertResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional // 각 테스트 후 롤백
public class ConcertFacadeIntegrationTest {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;

    private ConcertEntity concert;
    private ConcertScheduleEntity concertSchedule;
    private SeatEntity seat;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터 준비
        concert = ConcertEntity.builder()
                .concertName("Test Concert")
                .status(ConcertStatus.AVAILABLE)
                .build();
        concertRepository.save(concert); // 콘서트 저장

        concertSchedule = ConcertScheduleEntity.builder()
                .concertId(concert.getId())
                .performanceDay(LocalDateTime.now().plusDays(1)) // 내일
                .build();
        concertSchedule = concertScheduleRepository.save(concertSchedule); // 콘서트 스케줄 저장

        seat = SeatEntity.builder()
                .concertScheduleId(concertSchedule.getId())
                .seatNumber("A1")
                .status(SeatStatus.AVAILABLE)
                .seatPrice(10000L)
                .build();
        seatRepository.save(seat); // 좌석 저장
    }

    @Test
    public void testGetConcert() {
        // When
        List<ConcertResult.Concert> concerts = concertFacade.getConcert();

        // Then
        assertThat(concerts).isNotEmpty();
        assertThat(concerts.get(0).concertName()).isEqualTo("Test Concert");
    }

    @Test
    public void testGetConcertDate() {
        // Given
        Long concertId = concert.getId();
        LocalDateTime now = LocalDateTime.now();

        // When
        List<ConcertResult.ConcertDate> concertDates = concertFacade.getConcertDate(concertId, now);

        // Then
        assertThat(concertDates).isNotEmpty();
        assertThat(concertDates.get(0).performanceDay()).isEqualTo(concertSchedule.getPerformanceDay());
    }

    @Test
    public void testGetConcertSeat() {
        // Given
        String tokenValue = "valid-token"; // 유효한 토큰 값
        LocalDateTime now = LocalDateTime.now();


//        when(tokenService.isActive(any(String.class), any(LocalDateTime.class))).thenReturn(new TokenEntity(1L, TokenStatus.ACTIVE, "testToken", LocalDateTime.now().plusMinutes(10)));

        // When
        List<ConcertResult.ConcertSeat> concertSeats = concertFacade.getConcertSeat(concertSchedule.getId(), tokenValue, now);

        // Then
        assertThat(concertSeats).isNotEmpty();
        assertThat(concertSeats.get(0).seatNumber()).isEqualTo(seat.getSeatNumber());
        assertThat(concertSeats.get(0).seatStatus()).isEqualTo(seat.getStatus());
        assertThat(concertSeats.get(0).seatPrice()).isEqualTo(seat.getSeatPrice());
    }

    @Test
    public void testGetConcertSeat_InvalidToken() {
        // Given
        Long concertScheduleId = concertSchedule.getId();
        LocalDateTime now = LocalDateTime.now();
        String invalidToken = "invalid-token";

        // When & Then
        assertThatThrownBy(() -> concertFacade.getConcertSeat(concertScheduleId, invalidToken, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 토큰 입니다."); // 예외 메시지 확인
    }


}//end