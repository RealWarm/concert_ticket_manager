package com.hoonterpark.concertmanager.domain.enums;

public enum TokenStatus {
    PENDING,      // 대기중
    ACTIVE,       // 활성화
    RESERVED,     // 예약중
    PAID,         // 결제완료
    EXPIRED       // 만료
}