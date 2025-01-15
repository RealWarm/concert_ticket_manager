package com.hoonterpark.concertmanager.domain.entity;


import com.hoonterpark.concertmanager.domain.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long concertScheduleId;

    @Column(nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private Long seatPrice;

    private LocalDateTime expiredAt;

    @Builder
    private SeatEntity(Long concertScheduleId, String seatNumber, SeatStatus status, Long seatPrice, LocalDateTime expiredAt) {
        this.concertScheduleId = concertScheduleId;
        this.seatNumber = seatNumber;
        this.status = status;
        this.seatPrice = seatPrice;
        this.expiredAt = expiredAt;
    }

    public static SeatEntity create(Long concertScheduleId, String seatNumber, Long seatPrice, LocalDateTime now){
        return SeatEntity.builder()
                .concertScheduleId(concertScheduleId)
                .seatNumber(seatNumber)
                .status(SeatStatus.AVAILABLE)
                .seatPrice(seatPrice)
                .expiredAt(now.plusMinutes(10)) // 테스트의 편의를 위해 변수로 뺌
                .build();
    }

    // 좌석 예약하기 :: AVAILABLE인지 확인 후 RESERVED로 상태 변환
    public SeatEntity reserveSeat(LocalDateTime now) {
        if (this.status == SeatStatus.AVAILABLE) {
            this.status = SeatStatus.RESERVED;
            this.expiredAt = now.plusMinutes(10);
        } else {
            throw new RuntimeException("공석만 좌석 예약이 가능합니다.");
            // return false;
        }//if
        return this;
    }//reserveSeat


    // 좌석 결제하기
    // RESERVED 좌석이고 만료기한이 지나지 않았다면 예매
    public SeatEntity payForSeat(LocalDateTime now) {
        if (this.status == SeatStatus.RESERVED) {
            if (this.expiredAt.isBefore(now)) {
                this.status = SeatStatus.PAID;
                // return true;
            } else {
                // 아래 에러때문에 반영이 안될거 같은데...
                // 스케줄러가 잡아 주긴함
                this.status = SeatStatus.AVAILABLE;
                throw new RuntimeException("유효시간이 지났습니다.");
                // return false;
            }//if-2
        } else {
            // 공석 혹은 결제완료된 좌석입니다.
            throw new RuntimeException("예약된 좌석이 아닙니다.");
            // return false;
        }//if-1
        return this;
    }//payForSeat


    // 스케줄러
    // RESERVED 상태의 좌석중 유효시간이 지난좌석 활성화 시키기
    public Boolean releaseSeat(LocalDateTime now){
        if(this.status==SeatStatus.RESERVED){
            if(this.expiredAt.isAfter(now)){
                this.status=SeatStatus.AVAILABLE;
                return true;
            }else {
                // 아직 유효시간이 충분한 좌석입니다.
                return false;
            }//if-2
        }else {
            // 공석 혹은 결제완료된 좌석입니다.
            return false;
        }//if-1
    }//expireSeat


}//end
