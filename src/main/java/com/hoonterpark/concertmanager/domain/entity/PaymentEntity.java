package com.hoonterpark.concertmanager.domain.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationId;

    private Long amount;

    @Builder
    public PaymentEntity(Long reservationId, Long amount) {
        this.reservationId = reservationId;
        this.amount = amount;
    }

}
