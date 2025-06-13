package com.hoonterpark.concertmanager.interfaces.controller.api;

import com.hoonterpark.concertmanager.application.ConcertFacade;
import com.hoonterpark.concertmanager.application.result.ConcertResult;
import com.hoonterpark.concertmanager.interfaces.controller.api.response.ConcertHttpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "예약가능한 콘서트 조회")
    public ResponseEntity<List<ConcertHttpResponse.ConcertResponse>> getAvailableConcert() {
        return new ResponseEntity<>(ConcertHttpResponse.ConcertResponse.fromResult(concertFacade.getConcert()), HttpStatus.OK);
    }

    @GetMapping("/{concertId}/available-dates")
    @Operation(summary = "예약가능한 콘서트 날짜 조회")
    public ResponseEntity<List<ConcertResult.ConcertDate>> getAvailableDates(
            @PathVariable Long concertId
    ) {
        return new ResponseEntity<>(concertFacade.getConcertDate(concertId, LocalDateTime.now()), HttpStatus.OK);
    }

    @GetMapping("/{concertScheduleId}/available-seats")
    @Operation(summary = "예약 가능 좌석 조회", description = "예약 가능한 좌석 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 가능 좌석 성공.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<ConcertResult.ConcertSeat>> getAvailableSeats(
            @RequestHeader("Authorization") String token,
            @PathVariable Long concertScheduleId
    ) {
        return new ResponseEntity<>(concertFacade.getConcertSeat(concertScheduleId, token, LocalDateTime.now()), HttpStatus.OK);
    }
}


