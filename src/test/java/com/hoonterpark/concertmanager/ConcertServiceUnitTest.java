package com.hoonterpark.concertmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hoonterpark.concertmanager.domain.entity.ConcertEntity;
import com.hoonterpark.concertmanager.domain.entity.ConcertScheduleEntity;
import com.hoonterpark.concertmanager.domain.enums.ConcertStatus;
import com.hoonterpark.concertmanager.domain.repository.ConcertRepository;
import com.hoonterpark.concertmanager.domain.repository.ConcertScheduleRepository;
import com.hoonterpark.concertmanager.domain.service.ConcertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public class ConcertServiceUnitTest {

    @InjectMocks
    private ConcertService concertService;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    private ConcertEntity concert;
    private ConcertScheduleEntity concertSchedule;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        concert = ConcertEntity.builder()
                .id(1L)
                .concertName("Test Concert")
                .status(ConcertStatus.AVAILABLE)
                .build();

        concertSchedule = ConcertScheduleEntity.builder()
                .id(1L)
                .concertId(concert.getId())
                .performanceDay(LocalDateTime.now().plusDays(1)) // 내일
                .build();
    }

    @Test
    public void testFindConcertScheduleById() {
        // Given
        when(concertScheduleRepository.findById(concertSchedule.getId())).thenReturn(Optional.of(concertSchedule));

        // When
        ConcertScheduleEntity foundSchedule = concertService.findConcertScheduleById(concertSchedule.getId());

        // Then
        assertThat(foundSchedule).isNotNull();
        assertThat(foundSchedule.getId()).isEqualTo(concertSchedule.getId());
    }

    @Test
    public void testFindConcertScheduleById_NotFound() {
        // Given
        when(concertScheduleRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> concertService.findConcertScheduleById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 스케줄 입니다.");
    }

    @Test
    public void testFindAvailableConcerts() {
        // Given
        when(concertRepository.findByStatus(ConcertStatus.AVAILABLE)).thenReturn(Arrays.asList(concert));

        // When
        var availableConcerts = concertService.findAvailableConcerts();

        // Then
        assertThat(availableConcerts).isNotEmpty();
        assertThat(availableConcerts.get(0).getConcertName()).isEqualTo("Test Concert");
    }

    @Test
    public void testFindAvailableConcertSchedules() {
        // Given
        Long concertId = concert.getId();
        LocalDateTime now = LocalDateTime.now();
        when(concertScheduleRepository.findAvailableConcertSchedules(concertId, now)).thenReturn(Arrays.asList(concertSchedule));

        // When
        var availableSchedules = concertService.findAvailableConcertSchedules(concertId, now);

        // Then
        assertThat(availableSchedules).isNotEmpty();
        assertThat(availableSchedules.get(0).getConcertId()).isEqualTo(concertId);
    }

    @Test
    public void testFindAvailableConcertSchedules_NotAvailable() {
        // Given
        Long concertId = concert.getId();
        LocalDateTime now = LocalDateTime.now().plusDays(2); // 스케줄이 지나간 경우
        when(concertScheduleRepository.findAvailableConcertSchedules(concertId, now)).thenReturn(Arrays.asList(concertSchedule));

        // When
        var availableSchedules = concertService.findAvailableConcertSchedules(concertId, now);

        // Then
        assertThat(availableSchedules).isEmpty(); // 예약 가능한 스케줄이 없어야 함
    }
}
