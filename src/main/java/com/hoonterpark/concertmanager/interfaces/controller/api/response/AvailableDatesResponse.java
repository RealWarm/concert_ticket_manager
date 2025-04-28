package com.hoonterpark.concertmanager.interfaces.controller.api.response;

import lombok.Data;

@Data
public class AvailableDatesResponse {
    private Long concertOptionId;
    private String concertDate;
}
