package com.hoonterpark.concertmanager.interfaces.controller.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
public class ConcertDateDTO {
    Long concertOptionId;
    LocalDateTime concertDate;
}
