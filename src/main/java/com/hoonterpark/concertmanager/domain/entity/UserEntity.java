package com.hoonterpark.concertmanager.domain.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@ToString
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long point=0L;

    @Version
    private int version;

    @Builder
    public UserEntity(Long id, String name, Long point) {
        this.id = id;
        this.name = name;
        this.point = point;
    }

    @Builder
    private UserEntity(String name, Long point) {
        this.name = name;
        this.point = point;
    }

    public static UserEntity create(String name){
        return UserEntity.builder()
                .name(name)
                .point(0L)
                .build();
    }

    public static UserEntity create(String name, Long point){
        return UserEntity.builder()
                .name(name)
                .point(point)
                .build();
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
