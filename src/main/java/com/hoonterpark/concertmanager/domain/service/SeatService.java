package com.hoonterpark.concertmanager.domain.service;


import com.hoonterpark.concertmanager.domain.entity.SeatEntity;
import com.hoonterpark.concertmanager.domain.enums.SeatStatus;
import com.hoonterpark.concertmanager.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SeatService {
    private final Long EXPIRE_TIME=10l;
    private final SeatRepository seatRepository;

    // 좌석 생성하기
    public SeatEntity makeConcertReservationSeat(Long concertScheduleId, Long seatPrice){
        SeatEntity newSeat = SeatEntity.builder()
                .concertScheduleId(concertScheduleId)
                .seatNumber(concertScheduleId.toString() + LocalDateTime.now())
                .status(SeatStatus.AVAILABLE)
                .seatPrice(seatPrice)
                .expiredAt(LocalDateTime.now().plusMinutes(EXPIRE_TIME))
                .build();

        return seatRepository.save(newSeat);
    }

    // 좌석조회
    public List<SeatEntity> getConcertSeats(Long concertScheduleId){
        return seatRepository.findByConcertScheduleId(concertScheduleId);
    }


    // 1개의 좌석예약하기 By SeatId
    public SeatEntity reserveSeat(Long seatId, LocalDateTime now){
        SeatEntity toReserveSeat = seatRepository.findByIdWithLock(seatId)
                .orElseThrow(() -> new IllegalArgumentException("해당좌석은 없는 좌석입니다."));
        return seatRepository.save(toReserveSeat.reserveSeat(now));
    }


    // 좌석결제하기
    public SeatEntity payForSeat(Long seatId, LocalDateTime now){
        SeatEntity toPayForSeat = seatRepository.findByIdWithLock(seatId)
                .orElseThrow(() -> new IllegalArgumentException("해당좌석은 없는 좌석입니다."));
        return seatRepository.save(toPayForSeat.payForSeat(now));
    }


    // 좌석만료 스케줄링
    // RESERVED인 좌석을 조회해서 유효기간이 지났으면 AVAILABLE 바꾸기
    public List<SeatEntity> releaseSeat(LocalDateTime now){
        List<SeatEntity> reservedSeats = seatRepository.findReservedSeat();

        // 만료시킨 좌석만 필터링
        List<SeatEntity> expiredSeats = reservedSeats.stream()
                .filter(seat -> seat.releaseSeat(now))
                .collect(Collectors.toList());

        expiredSeats.forEach(seat -> seatRepository.save(seat)); // 만료시킨 좌석 저장

        return expiredSeats;
    }


}//end
