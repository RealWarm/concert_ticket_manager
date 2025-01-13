package com.hoonterpark.concertmanager.domain.repository;

import com.hoonterpark.concertmanager.domain.entity.PaymentEntity;

import java.util.List;

public interface PaymentRepository {

    PaymentEntity save(PaymentEntity payment);

    List<PaymentEntity> saveAll(List<PaymentEntity> payments);

}
