package com.hoonterpark.concertmanager.presentation.controller.request;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class ReservationRequest {
    private String token;
    private Long concertScheduleId;
    private Long seatId;
    private Long userId;
}