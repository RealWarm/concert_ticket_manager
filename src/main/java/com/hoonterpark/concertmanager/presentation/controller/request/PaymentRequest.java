package com.hoonterpark.concertmanager.presentation.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class PaymentRequest {
    private Long reservationId;
}