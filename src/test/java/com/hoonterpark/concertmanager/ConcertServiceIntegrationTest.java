package com.hoonterpark.concertmanager;

import com.hoonterpark.concertmanager.domain.entity.ConcertEntity;
import com.hoonterpark.concertmanager.domain.entity.ConcertScheduleEntity;
import com.hoonterpark.concertmanager.domain.enums.ConcertStatus;
import com.hoonterpark.concertmanager.domain.repository.ConcertRepository;
import com.hoonterpark.concertmanager.domain.repository.ConcertScheduleRepository;
import com.hoonterpark.concertmanager.domain.service.ConcertService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
@Transactional // 각 테스트 후 롤백
@SpringBootTest
public class ConcertServiceIntegrationTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;

    private ConcertEntity concert;
    private ConcertScheduleEntity concertSchedule;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터 준비
        concert = ConcertEntity.builder()
                .concertName("Test Concert")
                .status(ConcertStatus.AVAILABLE)
                .build();
        concertRepository.save(concert); // 콘서트 저장

        concertSchedule = ConcertScheduleEntity.builder()
                .concertId(concert.getId())
                .performanceDay(LocalDateTime.now().plusDays(1)) // 내일
                .build();
        concertScheduleRepository.save(concertSchedule); // 콘서트 스케줄 저장
    }

    @Test
    public void testFindConcertScheduleById() {
        // When
        ConcertScheduleEntity foundSchedule = concertService.findConcertScheduleById(concertSchedule.getId());

        // Then
        assertThat(foundSchedule).isNotNull();
        assertThat(foundSchedule.getId()).isEqualTo(concertSchedule.getId());
    }

    @Test
    public void testFindConcertScheduleById_NotFound() {
        // When & Then
        assertThatThrownBy(() -> concertService.findConcertScheduleById(999L)) // 존재하지 않는 ID
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 스케줄 입니다.");
    }

    @Test
    public void testFindAvailableConcerts() {
        // When
        List<ConcertEntity> availableConcerts = concertService.findAvailableConcerts();

        // Then
        assertThat(availableConcerts).isNotEmpty();
        assertThat(availableConcerts.get(0).getConcertName()).isEqualTo("Test Concert");
    }

    @Test
    public void testFindAvailableConcertSchedules() {
        // Given
        Long concertId = concert.getId();
        LocalDateTime now = LocalDateTime.now();

        // When

        List<ConcertScheduleEntity> availableSchedules = concertService.findAvailableConcertSchedules(concertId, now);
        log.info(concertScheduleRepository.findById(1L).get().toString());
        // Then
        assertThat(availableSchedules).isNotEmpty();
        assertThat(availableSchedules.get(0).getConcertId()).isEqualTo(concertId);
    }

    @Test
    public void testFindAvailableConcertSchedules_NotAvailable() {
        // Given
        Long concertId = concert.getId();
        LocalDateTime now = LocalDateTime.now().plusDays(2); // 스케줄이 지나간 경우

        // When
        List<ConcertScheduleEntity> availableSchedules = concertService.findAvailableConcertSchedules(concertId, now);

        // Then
        assertThat(availableSchedules).isEmpty(); // 예약 가능한 스케줄이 없어야 함
    }
}
