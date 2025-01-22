package com.hoonterpark.concertmanager.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.entity.TokenEntity;
import com.hoonterpark.concertmanager.domain.entity.UserEntity;
import com.hoonterpark.concertmanager.domain.enums.ReservationStatus;
import com.hoonterpark.concertmanager.domain.enums.SeatStatus;
import com.hoonterpark.concertmanager.domain.repository.ReservationRepository;
import com.hoonterpark.concertmanager.domain.repository.SeatRepository;
import com.hoonterpark.concertmanager.domain.repository.TokenRepository;
import com.hoonterpark.concertmanager.domain.repository.UserRepository;
import com.hoonterpark.concertmanager.domain.service.ReservationService;
import com.hoonterpark.concertmanager.domain.service.SeatService;
import com.hoonterpark.concertmanager.domain.service.TokenService;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.ReservationResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest
@Transactional // 각 테스트 후 롤백
public class ReservationFacadeIntegrationTest {

    @Autowired
    private ReservationFacade reservationFacade;

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
    private TokenRepository tokenRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testReserveSeat() {

        // Given
        SeatEntity seatA1 = seatRepository.save(SeatEntity.create(1L, "A1", 150000L, LocalDateTime.now()));

        UserEntity user = UserEntity.create("user1");
        userRepository.save(user);

        TokenEntity tokenEntity = TokenEntity.create(LocalDateTime.now(), 10);
        tokenEntity.activateToken(LocalDateTime.now());
        tokenRepository.save(tokenEntity);

        ReservationRequest request = new ReservationRequest(1L, seatA1.getId(), user.getId());


        // When
        ReservationResponse.Reservation response = reservationFacade.reserveSeat(request, tokenEntity.getTokenValue(), LocalDateTime.now());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.reservationId()).isNotNull();

        // 예약이 성공적으로 이루어졌는지 확인
        ReservationEntity reservation = reservationRepository.findById(response.reservationId()).orElseThrow();
        assertThat(reservation.getUserId()).isEqualTo(request.getUserId());
        assertThat(reservation.getSeatId()).isEqualTo(request.getSeatId());
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }

    @Test
    public void testReserveSeatMany() {

        // Given
        List<ReservationResponse.Reservation> responses = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            UserEntity user = UserEntity.create("user" + i);
            userRepository.save(user);

            TokenEntity tokenEntity = TokenEntity.create(LocalDateTime.now(), 10);
            tokenEntity.activateToken(LocalDateTime.now());
            tokenRepository.save(tokenEntity);

            SeatEntity seatA1 = seatRepository.save(SeatEntity.create(1L, "A1", 150000L, LocalDateTime.now()));


            ReservationRequest request = new ReservationRequest(1L, seatA1.getId(), user.getId());
            responses.add(reservationFacade.reserveSeat(request, tokenEntity.getTokenValue(), LocalDateTime.now()));
        }

        //then
        assertThat(responses).hasSize(10);
    }

    @Test
    public void testReleaseSeat() {
        // Given
        List<SeatEntity> seats = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            // AVAILABLE상태의 좌석 생성
            SeatEntity seatA1 = seatRepository.save(SeatEntity.create(1L, "A" + i, 150000L, LocalDateTime.now()));
            seatA1.reserveSeat(LocalDateTime.now()); // RESERVED로 상태 좌석 상태 변경
            seats.add(seatRepository.save(seatA1));
        }

        // When
        reservationFacade.releaseSeat(LocalDateTime.now()); // RESERVED 상태의 좌석을 AVAILABLE로 변경

        // Then
        int cnt = 0;
        for (int i = 0; i < seats.size(); i++) {
            if (seats.get(i).getStatus().equals(SeatStatus.AVAILABLE)) {
                cnt++;
            }
        }

        assertThat(cnt).isEqualTo(5);
    }


    @DisplayName("10명의 유저가 하나의 좌석을 동시에 예약한다. 1명만 예약이되고 나머지는 에러를 내뱉는다.")
    @Test
    public void testReservationConcurrency() throws InterruptedException {
        // given
        int threadCnt = 10;
        int expectedSuccessCnt = 1;
        int expectedFailCnt = 9;
        CountDownLatch latch = new CountDownLatch(threadCnt);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        AtomicInteger successCnt = new AtomicInteger();
        AtomicInteger failCnt = new AtomicInteger();

        // 1개의 좌석 만들기
        SeatEntity seatA1 = seatRepository.save(SeatEntity.create(1L, "A1", 150000L, LocalDateTime.now()));
        List<TokenEntity> tokens = new ArrayList<>();
        List<UserEntity> users = new ArrayList<>();
        List<ReservationRequest> requests = new ArrayList<>();

        //  10명의 유저 만들기 && 10개의 토큰 만들기
        for (int i = 1; i <= threadCnt; i++) {
            UserEntity user = UserEntity.create("user" + i);
            users.add(userRepository.save(user));

            TokenEntity tokenEntity = TokenEntity.create(LocalDateTime.now(), 10);
            tokenEntity.activateToken(LocalDateTime.now());
            tokens.add(tokenRepository.save(tokenEntity));

            ReservationRequest request = new ReservationRequest(1L, seatA1.getId(), user.getId());
            requests.add(request);
        }


        // when
        for (int i = 0; i < threadCnt; i++) {
            ReservationRequest request = requests.get(i);
            String token = tokens.get(i).getTokenValue();
            executorService.execute(() -> {
                reservationFacade.reserveSeat(request, token, LocalDateTime.now());
                try {
                    successCnt.getAndIncrement();
                } catch (Exception e) {
                    System.out.println("error !! " + e);
                    failCnt.getAndIncrement();
                } finally {
                    latch.countDown();
                }//try
            });
        }//for-i

        latch.await();
        executorService.shutdown();

        Optional<ReservationEntity> byId = reservationRepository.findById(1L);
        assertThat(successCnt.get()).isEqualTo(expectedSuccessCnt);
        assertThat(failCnt.get()).isEqualTo(expectedFailCnt);
    }

}
//        log.info("{} :: \nuser: {} \ntoken: {}\nreqeust{}", users.get(i), tokens.get(i), requests.get(i));
//        System.out.println(i + " :: \nuser:" + users.get(i) + " :: \ntoken: " + tokens.get(i) + "\nrequest: " + requests.get(i));
//        System.out.println(i + " :: \nuser:" + userRepository.findById(users.get(i).getId())
//        + " :: \ntoken: " + tokenRepository.findByTokenValue(tokens.get(i).getTokenValue())
//        + "\nrequest: " + requests.get(i));