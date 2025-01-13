package com.hoonterpark.concertmanager.presentation.controller.request;

import lombok.Getter;

@Getter
public class PaymentRequest {
    private String token;
    private Long reservationId;
}