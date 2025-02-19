package com.hoonterpark.concertmanager.domain.kafka;

import com.hoonterpark.concertmanager.infrastructure.kafka.payment.PaymentOutboxEvent;

import java.util.List;

public interface PaymentMessageOutboxWritter {
    public PaymentOutboxEvent save(PaymentOutboxEvent message);
    public PaymentOutboxEvent findById(Long id);
    public List<PaymentOutboxEvent> findByStatus(String status);
    public PaymentOutboxEvent findByAggregateId(Long aggregateId);
    public void complete(PaymentOutboxEvent message);
}
