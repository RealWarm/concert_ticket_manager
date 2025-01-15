package com.hoonterpark.concertmanager.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hoonterpark.concertmanager.domain.entity.UserEntity;
import com.hoonterpark.concertmanager.domain.repository.UserRepository;
import com.hoonterpark.concertmanager.domain.service.UserService;
import com.hoonterpark.concertmanager.presentation.controller.response.UserBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@SpringBootTest
public class UserUseCaseIntegrationTest {

    @Autowired
    private UserUseCase userUseCase;

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
        UserBalanceResponse response = userUseCase.getUserBalance(user.getId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBalance()).isEqualTo(user.getPoint());
    }

    @Test
    public void testChargeUserPoint() {
        // Given
        Long chargeAmount = 500L;

        // When
        UserBalanceResponse response = userUseCase.chargeUserPoint(user.getId(), chargeAmount);

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
        assertThatThrownBy(() -> userUseCase.chargeUserPoint(user.getId(), chargeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트 충전은 0보다 커야합니다."); // 예외 메시지 확인
    }

    @Test
    public void testChargeUserPoint_UserNotFound() {
        // Given
        Long nonExistentUserId = 999L; // 존재하지 않는 유저 ID
        Long chargeAmount = 500L;

        // When & Then
        assertThatThrownBy(() -> userUseCase.chargeUserPoint(nonExistentUserId, chargeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 유저 입니다."); // 예외 메시지 확인
    }


}
