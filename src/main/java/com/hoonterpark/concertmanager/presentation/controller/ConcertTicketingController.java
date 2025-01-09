package com.hoonterpark.concertmanager.presentation.controller;

import com.hoonterpark.concertmanager.presentation.controller.request.ChargeBalanceRequest;
import com.hoonterpark.concertmanager.presentation.controller.request.PaymentRequest;
import com.hoonterpark.concertmanager.presentation.controller.request.ReservationRequest;
import com.hoonterpark.concertmanager.presentation.controller.request.UserTokenRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api")
@Tag(name = "콘서트 컨트롤러", description = "콘서트 정보, 토큰, 예약, 결제, 충전 기능 다합쳐 있음 5주차에 분리예정")
public class ConcertTicketingController {


    @PostMapping("/token")
    @Operation(summary = "신규토큰 발행", description = "콘서트는 한개만 있다 가정")
    public ResponseEntity<CommonResponse<TokenResponse.Token>> issueToken(
            @RequestBody UserTokenRequest request
    ) {
        TokenResponse.Token dto = new TokenResponse.Token("TokenValue");

        CommonResponse<TokenResponse.Token> response = new CommonResponse<>();
        response.setResult("200");
        response.setMessage("Success");
        response.setData(dto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }//issueToken


    @GetMapping("/token")
    @Operation(summary = "대기열 조회", description = "")
    public ResponseEntity<TokenResponse.TokenQueueResponse> getQueueToken(
            @RequestParam String tokenValue
    ){
        return new ResponseEntity<>(new TokenResponse.TokenQueueResponse(tokenValue, 11), HttpStatus.OK);
    }


    @GetMapping("/concerts")
    @Operation(summary = "예약가능한 콘서트 날짜", description = "")
    public ResponseEntity<List<ConcertResponse.Concert>> getAvailableConcert(

    ) {
        ConcertResponse.Concert concert1 = new ConcertResponse.Concert("아이유 새해 콘서트");
        ConcertResponse.Concert concert2 = new ConcertResponse.Concert("성시경 신년 콘서트");
        return new ResponseEntity<>(List.of(concert1, concert2), HttpStatus.OK);
    }


    @GetMapping("/{concertId}/available-dates")
    @Operation(summary = "예약가능한 콘서트 날짜 조회", description = "")
    public ResponseEntity<List<ConcertDateDTO>> getAvailableDates(
            @PathVariable Long concertId,
            @RequestParam String token
    ) {
        List<ConcertDateDTO> options = new ArrayList<>();
        ConcertDateDTO concert1 = new ConcertDateDTO(1L, LocalDateTime.now().plusDays(11));
        ConcertDateDTO concert2 = new ConcertDateDTO(2L, LocalDateTime.now().plusDays(11));

        return new ResponseEntity<>(List.of(concert1, concert2), HttpStatus.OK);
    }


    @GetMapping("/{concertId}/available-seats")
    public ResponseEntity<List<SeatDTO>> getAvailableSeats(
            @PathVariable Long concertOptionId,
            @RequestParam String token
    ) {
        SeatDTO seat1 = new SeatDTO(1L, "A1", "Available");
        SeatDTO seat2 = new SeatDTO(2L, "A2", "Hold");
        return new ResponseEntity<>(List.of(seat1, seat2), HttpStatus.OK);
    }


    @PostMapping("/reserves")
    public ResponseEntity<CommonResponse<ReserveResponse>> reserveSeat(
            @RequestBody ReservationRequest request
    ) {
        CommonResponse<ReserveResponse> response = CommonResponse.<ReserveResponse>builder()
                .data(new ReserveResponse(1L))
                .message("Success")
                .result("200")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping("/balance/charge")
    public ResponseEntity<UserBalanceResponse> chargeBalance(
            @RequestBody ChargeBalanceRequest request
    ) {
        UserBalanceResponse response = new UserBalanceResponse(6000L);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/balance")
    public ResponseEntity<UserBalanceResponse> getBalance(
            @RequestParam Long userId
    ) {
        UserBalanceResponse response = new UserBalanceResponse(6000L);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/pay")
    public ResponseEntity<CommonResponse<PaymentResponse>> pay(
            @RequestBody PaymentRequest request
    ) {
        CommonResponse<PaymentResponse> response = new CommonResponse<>();
        response.setResult("200");
        response.setMessage("Success");
        response.setData(new PaymentResponse(456L));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}//end


