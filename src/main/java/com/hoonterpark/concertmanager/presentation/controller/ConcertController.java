package com.hoonterpark.concertmanager.presentation.controller;

import com.hoonterpark.concertmanager.application.*;
import com.hoonterpark.concertmanager.presentation.controller.request.ChargeBalanceRequest;
import com.hoonterpark.concertmanager.presentation.controller.request.PaymentRequest;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import com.hoonterpark.concertmanager.presentation.controller.request.UserTokenRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "콘서트 컨트롤러", description = "콘서트 조회, 날짜조회, 좌석조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConcertController {
    private final ConcertFacade concertFacade;


    @GetMapping("/concerts")
    @Operation(summary = "예약가능한 콘서트 조회", description = "")
    public ResponseEntity<List<ConcertResponse.Concert>> getAvailableConcert() {
        return new ResponseEntity<>(concertFacade.getConcert(), HttpStatus.OK);
    }


    @GetMapping("/{concertId}/available-dates")
    @Operation(summary = "예약가능한 콘서트 날짜 조회", description = "")
    public ResponseEntity<List<ConcertResponse.ConcertDate>> getAvailableDates(
            @PathVariable Long concertId
    ) {
        return new ResponseEntity<>(concertFacade.getConcertDate(concertId, LocalDateTime.now()), HttpStatus.OK);
    }


    @GetMapping("/{concertScheduleId}/available-seats")
    public ResponseEntity<List<ConcertResponse.ConcertSeat>> getAvailableSeats(
            @RequestHeader("Authorization") String token,
            @PathVariable Long concertScheduleId
    ) {
        return new ResponseEntity<>(concertFacade.getConcertSeat(concertScheduleId, token, LocalDateTime.now()), HttpStatus.OK);
    }




}//end


