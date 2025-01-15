package com.hoonterpark.concertmanager.presentation.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserTokenRequest {
    private Long userId;

}
