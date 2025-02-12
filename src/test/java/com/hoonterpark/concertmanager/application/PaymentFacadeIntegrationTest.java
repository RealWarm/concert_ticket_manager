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
import com.hoonterpark.concertmanager.domain.repository.TokenRepository;
import com.hoonterpark.concertmanager.domain.repository.UserRepository;
import com.hoonterpark.concertmanager.domain.service.*;
import com.hoonterpark.concertmanager.presentation.controller.request.PaymentRequest;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PaymentFacadeIntegrationTest {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

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

    @BeforeEach
    public void setUp() {
    }


    // 유저생성
    // 예약중인 토큰생성
    // 예약중인 좌석 생성
    // 예약생성
    @Test
    public void testMakePayment() {
        // Given
        UserEntity user = UserEntity.create("hoon", 20000L);
        userRepository.save(user); // 유저 저장

        LocalDateTime now = LocalDateTime.now();
        TokenEntity tokenEntity = tokenService.makeToken(now);// 토큰 발행
        tokenEntity.activateToken(now);
        tokenEntity.updateTokenToReserved(now);
        tokenRepository.save(tokenEntity);

        SeatEntity seat = SeatEntity.create(1L, "A1", 10000L, now);
        seat.reserveSeat(now);
        seatRepository.save(seat); // 좌석 저장

        ReservationRequest reservationRequest = ReservationRequest.builder()
                .concertScheduleId(1L)
                .seatId(seat.getId())
                .userId(user.getId())
                .build();

        ReservationEntity reservation = reservationService.makeReservation(reservationRequest, seat.getSeatPrice(), LocalDateTime.now());// 예약 저장

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .reservationId(reservation.getId())
                .build();

        // When
        PaymentResponse response = paymentFacade.makePayment(paymentRequest, tokenEntity.getTokenValue(), LocalDateTime.now());

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


    @Test
    public void 한명의_유저가_따닥_결제를_진행하면_1건만_결제된다() throws InterruptedException {
        // Given
        UserEntity user = userRepository.save(UserEntity.create("hoon", 20000L)); // 유저 저장

        LocalDateTime now = LocalDateTime.now();

        TokenEntity tokenEntity = tokenService.makeToken(now);// 토큰 발행
        tokenEntity.activateToken(now);
        tokenEntity.updateTokenToReserved(now);
        tokenRepository.save(tokenEntity);

        SeatEntity seat = SeatEntity.create(1L, "A1", 10000L, now);
        seat.reserveSeat(now);
        seatRepository.save(seat); // 좌석 저장

        ReservationRequest reservationRequest = ReservationRequest.builder()
                .concertScheduleId(1L)
                .seatId(seat.getId())
                .userId(user.getId())
                .build();

        ReservationEntity reservation = reservationService.makeReservation(reservationRequest, seat.getSeatPrice(), LocalDateTime.now());// 예약 저장

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .reservationId(reservation.getId())
                .build();


        int threadCnt = 10;
        int expectedSuccessCnt = 1;
        int expectedFailCnt = 9;
        CountDownLatch latch = new CountDownLatch(threadCnt);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        AtomicInteger successCnt = new AtomicInteger();
        AtomicInteger failCnt = new AtomicInteger();

        // when
        for (int i = 0; i < threadCnt; i++) {
            executorService.execute(() -> {
                try {
                    paymentFacade.makePayment(paymentRequest, tokenEntity.getTokenValue(), LocalDateTime.now());
                    successCnt.getAndIncrement();
                } catch (Exception e) {
                    failCnt.getAndIncrement();
                } finally {
                    latch.countDown();
                }//try
            });
        }//for-i

        latch.await();
        executorService.shutdown();


        // 1명 성공, 9명 실패
        assertThat(successCnt.get()).isEqualTo(expectedSuccessCnt);
        assertThat(failCnt.get()).isEqualTo(expectedFailCnt);

        // 결제가 성공적으로 이루어졌는지 확인
        ReservationEntity paidReservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(paidReservation.getStatus()).isEqualTo(ReservationStatus.PAID);

        // 좌석이 결제 상태로 변경되었는지 확인
        SeatEntity paidSeat = seatRepository.findById(seat.getId()).orElseThrow();
        assertThat(paidSeat.getStatus()).isEqualTo(SeatStatus.PAID);

        // 유저 잔액이 차감되었는지 확인
        UserEntity updatedUser = userService.findById(user.getId());
        assertThat(updatedUser.getPoint()).isEqualTo(10000L); // 20000 - 10000
    }


}
