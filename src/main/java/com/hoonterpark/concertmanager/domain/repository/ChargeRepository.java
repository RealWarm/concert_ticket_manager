package com.hoonterpark.concertmanager.domain.repository;

import com.hoonterpark.concertmanager.domain.entity.ChargeEntity;

import java.util.List;

public interface ChargeRepository {

    ChargeEntity save(ChargeEntity chargeHistory);

    List<ChargeEntity> saveAll(List<ChargeEntity> chargeHistories);

}
