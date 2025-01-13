package com.hoonterpark.concertmanager;

import com.hoonterpark.concertmanager.domain.entity.PaymentEntity;
import com.hoonterpark.concertmanager.domain.repository.PaymentRepository;
import com.hoonterpark.concertmanager.domain.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Transactional
@SpringBootTest
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    private Long reservationId;
    private Long amount = 10000L;

    @BeforeEach
    public void setUp() {
        // 예약 ID 설정 (테스트를 위해 임의의 값 사용)
        reservationId = 1L;
    }

    @Test
    public void testMakePayment() {
        // When
        PaymentEntity payment = paymentService.makePayment(reservationId, amount);

        // Then
        assertThat(payment).isNotNull();
        assertThat(payment.getReservationId()).isEqualTo(reservationId);
        assertThat(payment.getAmount()).isEqualTo(amount);
    }

    @Test
    public void testMakePayment_InvalidReservationId() {
        // Given
        Long invalidReservationId = null; // 잘못된 예약 ID

        // When & Then
        assertThatThrownBy(() -> paymentService.makePayment(invalidReservationId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약 ID는 null일 수 없습니다."); // 예외 메시지 확인
    }
}
