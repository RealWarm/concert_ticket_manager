package com.hoonterpark.concertmanager.domain.entity;


import com.hoonterpark.concertmanager.domain.enums.ReservationStatus;
import com.hoonterpark.concertmanager.domain.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long concertScheduleId;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Builder
    public ReservationEntity(Long userId, Long concertScheduleId, Long seatId, Long totalPrice, LocalDateTime expiredAt, ReservationStatus status) {
        this.userId = userId;
        this.concertScheduleId = concertScheduleId;
        this.seatId = seatId;
        this.totalPrice = totalPrice;
        this.expiredAt = expiredAt;
        this.status = status;
    }

    public static ReservationEntity create(Long userId, Long concertScheduleId, Long seatId, Long totalPrice, LocalDateTime now){
       return ReservationEntity.builder()
               .userId(userId)
               .concertScheduleId(concertScheduleId)
               .seatId(seatId)
               .totalPrice(totalPrice)
               .expiredAt(now.plusMinutes(10))
               .status(ReservationStatus.RESERVED)
               .build();
    }

    // RESERVED의 상태의 예약 && 유효시간이 아직 안지났으면
    public ReservationEntity payForReservation(LocalDateTime now){
        if(this.status==ReservationStatus.RESERVED){
            if(this.expiredAt.isAfter(now)){
                this.status=ReservationStatus.PAID;
            }else{
                // 유효시간 만료
                throw new RuntimeException("예약가능 시간이 지났습니다.");
            }//if-2
        }else {
            // PAID, EXPIRED, CANCLED 상태면 예약 불가하다!
            throw new RuntimeException("예약가능한 상태가 아닙니다.");
        }//if-1
        return this;
    }
}
