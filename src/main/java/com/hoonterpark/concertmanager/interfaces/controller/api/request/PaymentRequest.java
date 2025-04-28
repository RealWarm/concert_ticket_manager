package com.hoonterpark.concertmanager.interfaces.controller.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class PaymentRequest {
    private Long reservationId;
}