package com.hoonterpark.concertmanager.interfaces.controller.api.request;

import lombok.Data;

@Data
public class ChargeBalanceRequest {
    private Long userId;
    private Long amount;
}