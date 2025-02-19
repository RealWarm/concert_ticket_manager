package com.hoonterpark.concertmanager.domain.listener;

import com.hoonterpark.concertmanager.application.event.PaidEvent;
import com.hoonterpark.concertmanager.infrastructure.client.DataPlatformMockApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class PaidEventListener {
    private final ApplicationEventPublisher eventPublisher;
    private final DataPlatformMockApiClient dataPlatformMockApiClient;

    public PaidEventListener(DataPlatformMockApiClient dataPlatformMockApiClient, ApplicationEventPublisher eventPublisher) {
        this.dataPlatformMockApiClient = dataPlatformMockApiClient;
        this.eventPublisher = eventPublisher;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaidEvent(PaidEvent paidEvent) {
        try {
            // 예약 정보를 데이터 플랫폼으로 전달
            dataPlatformMockApiClient.sendReservationInfo(paidEvent.getReservation());
        } catch (Exception e) {
            log.error("예약 정보 전달에 실패하였습니다. 예약 정보: {}", paidEvent.getReservation(), e);
        }
    }
}