package com.hoonterpark.concertmanager.infrastructure;


import com.hoonterpark.concertmanager.domain.entity.ConcertScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertScheduleEntity, Long> {
    List<ConcertScheduleEntity> findByConcertId(Long concertId);

    @Query("SELECT cs FROM ConcertScheduleEntity cs WHERE cs.concertId = :concertId AND cs.performanceDay >= :now")
    List<ConcertScheduleEntity> findUpcomingConcerts(@Param("concertId") Long concertId, @Param("now") LocalDateTime now);

}
