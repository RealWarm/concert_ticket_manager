package com.hoonterpark.concertmanager.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.entity.UserEntity;
import com.hoonterpark.concertmanager.domain.enums.ReservationStatus;
import com.hoonterpark.concertmanager.domain.enums.SeatStatus;
import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import com.hoonterpark.concertmanager.domain.repository.ReservationRepository;
import com.hoonterpark.concertmanager.domain.repository.SeatRepository;
import com.hoonterpark.concertmanager.domain.repository.UserRepository;
import com.hoonterpark.concertmanager.domain.service.ReservationService;
import com.hoonterpark.concertmanager.domain.service.SeatService;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.ReservationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
public class ReservationUsecaseIntegrationTest {

    @Autowired
    private ReservationUsecase reservationUsecase;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private UserEntity user;
    private TokenEntity token;
    private SeatEntity seat;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터 준비
        user = UserEntity.builder()
                .name("Test User")
                .build();
        userRepository.save(user); // 유저 저장

        token = TokenEntity.builder()
                .status(TokenStatus.ACTIVE)
                .tokenValue("valid-token")
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
        tokenService.issueToken(LocalDateTime.now()); // 토큰 발행

        seat = SeatEntity.builder()
                .concertScheduleId(1L)
                .seatNumber("A1")
                .status(SeatStatus.AVAILABLE)
                .seatPrice(10000L)
                .build();
        seatRepository.save(seat); // 좌석 저장
    }

    @Test
    public void testReserveSeat() {
        // Given
        ReservationRequest request = ReservationRequest.builder()
                .userId(user.getId())
                .token(token.getTokenValue())
                .seatId(seat.getId())
                .concertScheduleId(seat.getConcertScheduleId())
                .build();

        // When
        ReservationResponse.Reservation response = reservationUsecase.reserveSeat(request, LocalDateTime.now());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.reservationId()).isNotNull();

        // 예약이 성공적으로 이루어졌는지 확인
        ReservationEntity reservation = reservationRepository.findById(response.reservationId()).orElseThrow();
        assertThat(reservation.getUserId()).isEqualTo(user.getId());
        assertThat(reservation.getSeatId()).isEqualTo(seat.getId());
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }

    @Test
    public void testReleaseSeat() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        reservationUsecase.releaseSeat(now);

        // Then
        // 좌석이 정상적으로 해제되었는지 확인하는 로직이 필요합니다.
        // 예를 들어, SeatService의 releaseSeat 메서드가 호출되었는지 확인할 수 있습니다.
        verify(seatService).releaseSeat(now);
    }
}
