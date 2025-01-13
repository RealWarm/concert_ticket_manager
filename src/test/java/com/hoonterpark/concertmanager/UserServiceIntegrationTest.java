package com.hoonterpark.concertmanager;

import com.hoonterpark.concertmanager.domain.entity.UserEntity;
import com.hoonterpark.concertmanager.domain.repository.UserRepository;
import com.hoonterpark.concertmanager.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class UserServiceIntegrationTest {

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
    public void testFindById() {
        // When
        UserEntity foundUser = userService.findById(user.getId());

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
    }

    @Test
    public void testFindById_NotFound() {
        // When & Then
        assertThatThrownBy(() -> userService.findById(999L)) // 존재하지 않는 유저 ID
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 유저 입니다.");
    }

    @Test
    public void testChargePoint() {
        // Given
        Long chargeAmount = 500L;

        // When
        UserEntity updatedUser = userService.chargePoint(user.getId(), chargeAmount);

        // Then
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getPoint()).isEqualTo(1500L); // 1000 + 500
    }

    @Test
    public void testChargePoint_InvalidAmount() {
        // Given
        Long chargeAmount = -100L; // 잘못된 포인트 충전 금액

        // When & Then
        assertThatThrownBy(() -> userService.chargePoint(user.getId(), chargeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트 충전은 0보다 커야합니다."); // 예외 메시지 확인
    }

    @Test
    public void testPayment() {
        // Given
        Long payAmount = 500L;

        // When
        UserEntity updatedUser = userService.payment(user.getId(), payAmount);

        // Then
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getPoint()).isEqualTo(500L); // 1000 - 500
    }

    @Test
    public void testPayment_InsufficientFunds() {
        // Given
        Long payAmount = 1500L; // 잔액 초과

        // When & Then
        assertThatThrownBy(() -> userService.payment(user.getId(), payAmount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("잔액이 부족합니다."); // 예외 메시지 확인
    }

    @Test
    public void testPayment_InvalidAmount() {
        // Given
        Long payAmount = -100L; // 잘못된 결제 금액

        // When & Then
        assertThatThrownBy(() -> userService.payment(user.getId(), payAmount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("포인트 사용은 0원 이상만 가능합니다."); // 예외 메시지 확인
    }
}
