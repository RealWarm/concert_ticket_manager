package com.hoonterpark.concertmanager.application;

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
import com.hoonterpark.concertmanager.domain.service.*;
import com.hoonterpark.concertmanager.presentation.controller.request.PaymentRequest;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
public class PaymentUseCaseIntegrationTest {

    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private UserService userService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PaymentService paymentService;

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
    private ReservationEntity reservation;
    private SeatEntity seat;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터 준비
        user = UserEntity.builder()
                .name("Test User")
                .point(20000L) // 충분한 잔액
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

        reservation = ReservationEntity.builder()
                .userId(user.getId())
                .concertScheduleId(seat.getConcertScheduleId())
                .seatId(seat.getId())
                .totalPrice(seat.getSeatPrice())
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .status(ReservationStatus.RESERVED)
                .build();

        ReservationRequest request = ReservationRequest.builder()
                .concertScheduleId(1L)
                .token("testToken")
                .seatId(1L)
                .userId(1L)
                .build();

        reservationService.makeReservation(request, seat.getSeatPrice(), LocalDateTime.now()); // 예약 저장
    }

    @Test
    public void testMakePayment() {
        // Given
        PaymentRequest request = PaymentRequest.builder()
                .token(token.getTokenValue())
                .reservationId(reservation.getId())
                .build();

        // When
        PaymentResponse response = paymentUseCase.makePayment(request, LocalDateTime.now());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentId()).isNotNull();

        // 결제가 성공적으로 이루어졌는지 확인
        ReservationEntity paidReservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(paidReservation.getStatus()).isEqualTo(ReservationStatus.PAID);

        SeatEntity paidSeat = seatRepository.findById(seat.getId()).orElseThrow();
        assertThat(paidSeat.getStatus()).isEqualTo(SeatStatus.PAID);

        // 유저 잔액이 차감되었는지 확인
        UserEntity updatedUser = userService.findById(user.getId());
        assertThat(updatedUser.getPoint()).isEqualTo(10000L); // 20000 - 10000
    }
}
