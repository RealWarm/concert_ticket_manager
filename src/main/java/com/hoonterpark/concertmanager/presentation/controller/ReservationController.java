package com.hoonterpark.concertmanager.presentation.controller;


import com.hoonterpark.concertmanager.application.ReservationFacade;
import com.hoonterpark.concertmanager.application.result.ReservationResult;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.CommonResponse;
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
    public ResponseEntity<CommonResponse<ReservationResult.Reservation>> reserveSeat(
            @RequestHeader("QueueToken") String token,
            @RequestBody ReservationRequest request
    ) {
        CommonResponse<ReservationResult.Reservation> response = CommonResponse.<ReservationResult.Reservation>builder()
                                                                                    .data(reservationFacade.reserveSeat(request, token, LocalDateTime.now()))
                                                                                    .message("Success")
                                                                                    .result("200")
                                                                                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
