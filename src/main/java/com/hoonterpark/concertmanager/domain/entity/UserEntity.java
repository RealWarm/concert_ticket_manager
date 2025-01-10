package com.hoonterpark.concertmanager.domain.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long point=0L;

    @Builder
    public UserEntity(Long id, String name, Long point) {
        this.id = id;
        this.name = name;
        this.point = point;
    }


    public void chargePoint(Long point) {
        if (point <= 0) {
            throw new IllegalArgumentException("포인트 충전은 0보다 커야합니다.");
        }
        this.point += point;
    }//chargePoint


    public void pay(Long amount) {
        if (amount < 0) {
            throw new IllegalStateException("포인트 사용은 0원 이상만 가능합니다.");
        }

        if (amount > this.point) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }
        this.point -= amount;
    }//usePoint


}//end
