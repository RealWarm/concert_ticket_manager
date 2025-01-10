package com.hoonterpark.concertmanager.domain.entity;


import com.hoonterpark.concertmanager.domain.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;


@Slf4j
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    @Column(nullable = false)
    private String tokenValue;

    private LocalDateTime expiredAt;


    // 유닛 테스트를 위한 용도
    @Builder
    public TokenEntity(Long id, TokenStatus status, String tokenValue, LocalDateTime expiredAt) {
        this.id = id;
        this.status = status;
        this.tokenValue = tokenValue;
        this.expiredAt = expiredAt;
    }

    @Builder
    public TokenEntity(TokenStatus status, String tokenValue, LocalDateTime expiredAt) {
        this.status = status;
        this.tokenValue = tokenValue;
        this.expiredAt = expiredAt;
    }

    public Boolean isActive(LocalDateTime now) {
        if (this.status == TokenStatus.ACTIVE) {
            log.info("expiredAt :: {} ", this.expiredAt);
            log.info("now :: {} ", now);
            if (this.expiredAt.isAfter(now)) {
                return true;
            } else {
                throw new RuntimeException("예약가능한 유효시간이 지났습니다.");
            }//if-2
        } else {
            throw new RuntimeException("ACTIVE 상태가 아닙니다.");
        }//if-1
    }//isActive


    public Boolean activateToken(LocalDateTime now) {
        // PENDING 상태의 토큰이 유효시간 내일때
        // 상태를 PENDING > ACTIVE로 바꾼다.
        if (this.status == TokenStatus.PENDING) {
            if (this.expiredAt.isAfter(now)) {
                this.status = TokenStatus.ACTIVE;
                this.expiredAt = now.plusMinutes(10);
                return true;
            } else {
                this.status = TokenStatus.EXPIRED;
                // throw new RuntimeException("ACTIVE 상태의 토큰만 예약가능합니다.");
                return false;
            }//if-2
        } else {
            // throw new RuntimeException("ACTIVE 상태의 토큰만 예약가능합니다.");
            return false;
        }//if-1
    }//activateToken


    public Boolean expireToken(LocalDateTime now) {
        // PENDING, ACTIVE, RESERVED 인 토큰중에서
        // ExpiredAt을 지난 토큰의 상태를 EXPIRED로 바꾼다.
        if (this.status == TokenStatus.PENDING
                || this.status == TokenStatus.ACTIVE
                || this.status == TokenStatus.RESERVED) {
            log.info("expiredAt :: {} ", this.expiredAt);
            log.info("now :: {} ", now);
            if (this.expiredAt.isBefore(now)) {
                status = TokenStatus.EXPIRED;
                return true;
            } else {
                // 아직 유요한 토큰입니다.
                return false;
            }//if-2
        } else {
            // 이미 "결제완료 OR 만료"된 토큰입니다.
            return false;
        }//if-1
    }//expireToken


    public Boolean updateTokenToReserved(LocalDateTime now) {
        // ACTIVE 토큰이 예약을 하면 RESERVED으로 상태를 변환한다.
        // 유효 시간내 일때만 RESERVED으로 상태를 변환
        if (this.status == TokenStatus.ACTIVE) {
            if (this.expiredAt.isAfter(now)) {
                this.status = TokenStatus.RESERVED;
                this.expiredAt = now.plusMinutes(10);
                return true;
            } else {
                this.status = TokenStatus.EXPIRED;
                // throw new RuntimeException("예약가능한 유효시간이 지났습니다.");
                return false;
            }//if-2
        } else {
            // throw new RuntimeException("ACTIVE 상태의 토큰만 예약가능합니다.");
            return false;
        }//if-1
    }//updateTokenToReserved


    // 토큰이 예약 상태고,
    public Boolean updateTokenToPaid(LocalDateTime now) {
        if (this.status == TokenStatus.RESERVED) {
            if (this.expiredAt.isAfter(now)) {
                this.status = TokenStatus.PAID;
                return true;
            } else {
                this.status = TokenStatus.EXPIRED;
                throw new RuntimeException("결제 가능한 유효시간이 지났습니다.");
            }//if-2
        } else {
            throw new RuntimeException("RESERVED 상태의 토큰만 예약가능합니다.");
        }//if-1
    }//updateTokenToPaid

}//end
