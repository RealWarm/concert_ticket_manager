package com.hoonterpark.concertmanager.domain.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoonterpark.concertmanager.domain.entity.ReservationEntity;
import com.hoonterpark.concertmanager.domain.enums.PaymentOutBoxEventStatus;
import com.hoonterpark.concertmanager.infrastructure.client.DataPlatformMockApiClient;
import com.hoonterpark.concertmanager.infrastructure.kafka.payment.PaymentOutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private final DataPlatformMockApiClient dataPlatformMockApiClient;
    private final PaymentMessageOutboxWritter paymentMessageOutboxWritter;

    @KafkaListener(topics = "${topic.payment}", groupId = "")
    @Transactional
    public void listenPaymentCreated(String message){
        try {
            ReservationEntity reservation = objectMapper.readValue(message, ReservationEntity.class);

            // 예약 정보를 데이터 플랫폼으로 전달
            dataPlatformMockApiClient.sendReservationInfo(reservation);

            PaymentOutboxEvent outboxEvent = paymentMessageOutboxWritter.findByAggregateId(reservation.getId());
            outboxEvent.setStatus(PaymentOutBoxEventStatus.SUCCESS.name());
            paymentMessageOutboxWritter.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
