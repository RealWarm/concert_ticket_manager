package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.ChargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeJpaRepository extends JpaRepository<ChargeEntity, Long> {
}
