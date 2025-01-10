package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.ConcertEntity;
import com.hoonterpark.concertmanager.domain.enums.ConcertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcertJpaRepository extends JpaRepository<ConcertEntity, Long> {

    Optional<ConcertEntity> findByConcertName(String concertName);

    List<ConcertEntity> findByStatus(ConcertStatus status);

}
