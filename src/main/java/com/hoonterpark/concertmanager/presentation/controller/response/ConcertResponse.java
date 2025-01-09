package com.hoonterpark.concertmanager.presentation.controller.response;

import com.hoonterpark.concertmanager.domain.enums.SeatStatus;

import java.time.LocalDateTime;

public class ConcertResponse {

    public record Concert(
            String concertName
    ) {
    }

    public record ConcertDate(
            LocalDateTime performanceDay
    ) {
    }


    public record ConcertSeat(
            Long seatId,
            String seatNumber,
            SeatStatus seatStatus,
            Long seatPrice
    ) {
    }


}
