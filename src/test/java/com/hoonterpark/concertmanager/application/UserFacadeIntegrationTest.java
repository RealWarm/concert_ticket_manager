package com.hoonterpark.concertmanager.application;

import com.hoonterpark.concertmanager.common.error.CustomException;
import com.hoonterpark.concertmanager.domain.entity.UserEntity;
import com.hoonterpark.concertmanager.domain.repository.UserRepository;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.interfaces.controller.api.response.UserBalanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



@Slf4j
@SpringBootTest
public class UserFacadeIntegrationTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터 준비
        user = UserEntity.builder()
                .name("Test User")
                .point(1000L) // 초기 포인트 설정
                .build();
        userRepository.save(user); // 유저 저장
    }

    @Test
    public void testGetUserBalance() {
        // When
        UserBalanceResponse response = userFacade.getUserBalance(user.getId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBalance()).isEqualTo(user.getPoint());
    }

    @Test
    public void testChargeUserPoint() {
        // Given
        Long chargeAmount = 500L;

        // When
        UserBalanceResponse response = userFacade.chargeUserPoint(user.getId(), chargeAmount);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBalance()).isEqualTo(1500L);

        // 포인트가 올바르게 충전되었는지 확인
        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getPoint()).isEqualTo(1500L); // 1000 + 500
    }

    @Test
    public void testChargeUserPoint_InvalidAmount() {
        // Given
        Long chargeAmount = -100L; // 잘못된 포인트 충전 금액

        // When & Then
        assertThatThrownBy(() -> userFacade.chargeUserPoint(user.getId(), chargeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트 충전은 0보다 커야합니다."); // 예외 메시지 확인
    }

    @Test
    public void testChargeUserPoint_UserNotFound() {
        // Given
        Long nonExistentUserId = 999L; // 존재하지 않는 유저 ID
        Long chargeAmount = 500L;

        // When & Then
        assertThatThrownBy(() -> userFacade.chargeUserPoint(nonExistentUserId, chargeAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 유저입니다."); // 예외 메시지 확인
    }

    @Test
    public void 한명의_유저가_동시에_여러번_따닥_충전을_하면_한번만_되게한다() throws InterruptedException {
        // Given
        Long chargeAmount = 10000L;
        UserEntity chargingUser = UserEntity.create("hoon", 0L);
        userRepository.save(chargingUser);
        Long id = chargingUser.getId(); // 존재하지 않는 유저입니다 에러발생


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
                    userFacade.chargeUserPoint(id, chargeAmount);
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


        assertThat(successCnt.get()).isEqualTo(expectedSuccessCnt);
        assertThat(failCnt.get()).isEqualTo(expectedFailCnt);

    }


}
