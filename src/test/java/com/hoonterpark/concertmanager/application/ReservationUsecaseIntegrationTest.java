package com.hoonterpark.concertmanager.application;

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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // H2 데이터베이스 사용
@Transactional // 각 테스트 후 롤백
public class ReservationUsecaseIntegrationTest {
//
//    @Autowired
//    private ReservationUsecase reservationUsecase;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private TokenService tokenService;
//
//    @Autowired
//    private SeatService seatService;
//
//    @Autowired
//    private ReservationService reservationService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private SeatRepository seatRepository;
//
//    @Autowired
//    private TokenRepository tokenRepository;
//
//    @Autowired
//    private ReservationRepository reservationRepository;
//
//
//    @BeforeEach
//    public void setUp() {
//        for (int i = 1; i <= 4; i++) {
//            UserEntity user = UserEntity.create("user" + i);
//            userRepository.save(user);
//        }
//    }
//
//    @Test
//    public void testReserveSeat() {
//
//        // Given
//        SeatEntity seatA1 = seatRepository.save(SeatEntity.create(1L, "A1", 150000L, LocalDateTime.now()));
//        List<TokenEntity> tokens = new ArrayList<>();
//        List<UserEntity> users = new ArrayList<>();
//        List<ReservationRequest> requests = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            UserEntity user = UserEntity.create("user" + i);
//            userRepository.save(user);
//            users.add(user);
//
//            TokenEntity tokenEntity = TokenEntity.create(LocalDateTime.now(), 10);
//            tokenEntity.activateToken(LocalDateTime.now());
//            tokenRepository.save(tokenEntity);
//            tokens.add(tokenEntity);
//
//            requests.add(new ReservationRequest(tokenEntity.getTokenValue(), 1L,
//                    seatA1.getId(), user.getId()));
//        }
//
//        // When
//        ReservationResponse.Reservation response = reservationUsecase.reserveSeat(requests.get(0), LocalDateTime.now());
//
//        // Then
//        assertThat(response).isNotNull();
//        assertThat(response.reservationId()).isNotNull();
//
//        // 예약이 성공적으로 이루어졌는지 확인
//        ReservationEntity reservation = reservationRepository.findById(response.reservationId()).orElseThrow();
//        assertThat(reservation.getUserId()).isEqualTo(requests.get(0).getUserId());
//        assertThat(reservation.getSeatId()).isEqualTo(requests.get(0).getSeatId());
//        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
//    }
//
//    @Test
//    public void testReserveSeatMany() {
//
//        // Given
//        List<ReservationResponse.Reservation> responses = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            UserEntity user = UserEntity.create("user" + i);
//            userRepository.save(user);
//
//            TokenEntity tokenEntity = TokenEntity.create(LocalDateTime.now(), 10);
//            tokenEntity.activateToken(LocalDateTime.now());
//            tokenRepository.save(tokenEntity);
//
//            SeatEntity seatA1 = seatRepository.save(SeatEntity.create(1L, "A1", 150000L, LocalDateTime.now()));
//
//
//            ReservationRequest request = new ReservationRequest(tokenEntity.getTokenValue(), 1L, seatA1.getId(), user.getId());
//            responses.add(reservationUsecase.reserveSeat(request, LocalDateTime.now()));
//        }
//
//        //then
//        assertThat(responses).hasSize(10);
//    }
//
//    @Test
//    public void testReleaseSeat() {
//        // Given
//        List<SeatEntity> seats = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            SeatEntity seatA1 = seatRepository.save(SeatEntity.create(1L, "A1", 150000L, LocalDateTime.now().minusHours(2)));
//            seats.add(seatRepository.save(seatA1));
//        }
//
//        for (int i = 0; i < 5; i++) {
//            SeatEntity seatA1 = seatRepository.save(SeatEntity.create(1L, "A1", 150000L, LocalDateTime.now()));
//            seatA1.reserveSeat(LocalDateTime.now());
//            seatA1.payForSeat(LocalDateTime.now());
//            seats.add(seatRepository.save(seatA1));
//        }
//
//        // When
//        reservationUsecase.releaseSeat(LocalDateTime.now());
//
//        // Then
//        int cnt=0;
//        for (int i = 0; i <seats.size(); i++) {
//            if(seats.get(i).getStatus().equals(SeatStatus.AVAILABLE)){
//                cnt++;
//            }
//        }
//
//        assertThat(cnt).isEqualTo(5);
//
//    }
//
//    @Test
//    public void testReservationConcurrency2() {
//
//        // Given
//        int threadCnt = 1;
//        CountDownLatch latch = new CountDownLatch(threadCnt);
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
//
//        List<ReservationRequest> requests = new ArrayList<>();
//        SeatEntity seatA1 = seatRepository.save(SeatEntity.create(1L, "A1", 150000L, LocalDateTime.now()));
//        List<ReservationResponse.Reservation> responses = new ArrayList<>();
//        for (int i = 1; i <= 4; i++) {
//            UserEntity user = UserEntity.create("user" + i);
//            userRepository.save(user);
//
//            TokenEntity tokenEntity = TokenEntity.create(LocalDateTime.now(), 10);
//            tokenEntity.activateToken(LocalDateTime.now());
//            tokenRepository.save(tokenEntity);
//
//
//            ReservationRequest request = new ReservationRequest(tokenEntity.getTokenValue(), 1L, seatA1.getId(), user.getId());
//            requests.add(request);
////            responses.add(reservationUsecase.reserveSeat(request, LocalDateTime.now()));
//        }
//
//        for (int i = 0; i < threadCnt; i++) {
//            ReservationRequest request = requests.get(i);
//            executorService.execute(() -> {
//                reservationUsecase.reserveSeat(request, LocalDateTime.now());
//                try {
//
//
//                } catch (Exception e) {
//
//                } finally {
//                    latch.countDown();
//                }//try
//            });
//        }//for-i
//
//        //then
//        assertThat(responses).hasSize(4);
//    }
//
//    @DisplayName("10명의 유저가 하나의 좌석을 동시에 예약한다. 1명만 예약이되고 나머지는 에러를 내뱉는다.")
//    @Transactional
//    @Test
//    public void testReservationConcurrency() throws InterruptedException {
//        // given
//        int threadCnt = 1;
//        int expectedSuccessCnt = 1;
//        int expectedFailCnt = 9;
//        CountDownLatch latch = new CountDownLatch(threadCnt);
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
//        AtomicInteger successCnt = new AtomicInteger();
//        AtomicInteger failCnt = new AtomicInteger();
//
//        // 10명의 유저 만들기 && 10개의 토큰 만들기 & 1개의 좌석 만들기
//        SeatEntity seatA1 = seatRepository.save(SeatEntity.create(1L, "A1", 150000L, LocalDateTime.now()));
//        List<TokenEntity> tokens = new ArrayList<>();
//        List<UserEntity> users = new ArrayList<>();
//        List<ReservationRequest> requests = new ArrayList<>();
//
//        for (int i = 1; i <= 1; i++) {
//            UserEntity user = UserEntity.create("user" + i);
//            user=userRepository.save(user);
//            users.add(user);
//
//            TokenEntity token = TokenEntity.create(LocalDateTime.now(), 10);
//            token.activateToken(LocalDateTime.now());
//            token=tokenRepository.save(token);
//            tokens.add(token);
//            log.info("{} @@ user :: {}, token :: {}", i, user, token);
//
//            requests.add(new ReservationRequest(token.getTokenValue(), 1L,
//                                                seatA1.getId(), user.getId()));
//        }
//
//
//        // when
//        for (int i = 0; i < threadCnt; i++) {
//            ReservationRequest request = requests.get(i);
//            executorService.execute(() -> {
//                reservationUsecase.reserveSeat(request, LocalDateTime.now());
//                try {
//
//                    successCnt.getAndIncrement();
//                } catch (Exception e) {
//                    failCnt.getAndIncrement();
//                } finally {
//                    latch.countDown();
//                }//try
//            });
//        }//for-i
//
//        latch.await();
//        executorService.shutdown();
//
//        Optional<ReservationEntity> byId = reservationRepository.findById(1L);
//        log.info("@@@@@@@@@ {} ", byId.get());
//
////        assertThat(registrations).hasSize(expectedSuccessCnt);
////        assertThat(testedLecture.getCurrentCapacity()).isEqualTo(expectedSuccessCnt);
//        assertThat(successCnt.get()).isEqualTo(expectedSuccessCnt);
//        assertThat(failCnt.get()).isEqualTo(expectedFailCnt);
//    }

}
