package com.hoonterpark.concertmanager.presentation.controller;


import com.hoonterpark.concertmanager.application.ReservationFacade;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.CommonResponse;
import com.hoonterpark.concertmanager.presentation.controller.response.ReservationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Tag(name = "예약 컨트롤러", description = "예약 생성")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {
    private final ReservationFacade reservationFacade;


    @PostMapping("/reserves")
    public ResponseEntity<CommonResponse<ReservationResponse.Reservation>> reserveSeat(
            @RequestHeader("Authorization") String token,
            @RequestBody ReservationRequest request
    ) {
        CommonResponse<ReservationResponse.Reservation> response = CommonResponse.<ReservationResponse.Reservation>builder()
                .data(reservationFacade.reserveSeat(request, token, LocalDateTime.now()))
                .message("Success")
                .result("200")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
