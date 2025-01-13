package com.hoonterpark.concertmanager.presentation.controller.request;

import lombok.Data;

@Data
public class ChargeBalanceRequest {
    private Long userId;
    private Long amount;
}