package com.hoonterpark.concertmanager.infrastructure.kafka.payment;

import com.hoonterpark.concertmanager.domain.kafka.PaymentMessageOutboxWritter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentMessageOutboxWritterImpl implements PaymentMessageOutboxWritter {
    private final PaymentJpaOutboxRepository paymentJpaOutboxRepository;


    @Override
    public PaymentOutboxEvent save(PaymentOutboxEvent message) {
        return paymentJpaOutboxRepository.save(message);
    }

    @Override
    public PaymentOutboxEvent findById(Long id) {
        return paymentJpaOutboxRepository.findById(id).orElse(null);
    }

    @Override
    public List<PaymentOutboxEvent> findByStatus(String status) {
        return paymentJpaOutboxRepository.findAllByStatus(status);
    }

    @Override
    public PaymentOutboxEvent findByAggregateId(Long aggregateId) {
        return paymentJpaOutboxRepository.findByAggregateId(aggregateId).orElseThrow(() -> new RuntimeException("발행 내역이 없습니다."));
    }

    @Override
    public void complete(PaymentOutboxEvent message) {
        paymentJpaOutboxRepository.delete(message);
    }
}
