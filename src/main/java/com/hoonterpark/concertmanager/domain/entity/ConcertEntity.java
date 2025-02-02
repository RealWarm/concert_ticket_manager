package com.hoonterpark.concertmanager.domain.entity;


import com.hoonterpark.concertmanager.domain.enums.ConcertStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "concerts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String concertName;

    @Enumerated(EnumType.STRING)
    private ConcertStatus status;

    //단위테스트용
    @Builder
    public ConcertEntity(Long id, String concertName, ConcertStatus status) {
        this.id = id;
        this.concertName = concertName;
        this.status = status;
    }

    @Builder
    public ConcertEntity(String concertName, ConcertStatus status) {
        this.concertName = concertName;
        this.status = status;
    }

    // 예매가능하니?
    public Boolean isAvailable(){
        if(this.status==ConcertStatus.AVAILABLE){
            return true;
        }else{
            return false;
        }
    }//isAvailable


}
