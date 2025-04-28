package com.hoonterpark.concertmanager.interfaces.controller.api.request;

import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class ReservationRequest {
    private Long concertScheduleId;
    private Long seatId;
    private Long userId;
}