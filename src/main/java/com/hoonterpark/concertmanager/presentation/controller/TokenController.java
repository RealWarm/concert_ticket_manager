package com.hoonterpark.concertmanager.presentation.controller;


import com.hoonterpark.concertmanager.application.TokenFacade;
import com.hoonterpark.concertmanager.presentation.controller.request.UserTokenRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.hoonterpark.concertmanager.application.result.TokenResult.TokenQueue;


@Tag(name = "토큰 컨트롤러", description = "토큰 발급, 대기열 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenController {
    private final TokenFacade tokenFacade;


    @PostMapping("/token")
    @Operation(summary = "신규토큰 발행", description = "콘서트는 한개만 있다 가정")
    public ResponseEntity<CommonResponse<TokenQueue>> issueToken(
            @RequestBody UserTokenRequest request
    ) {
        CommonResponse<TokenQueue> response = new CommonResponse<>();
        response.setResult("200");
        response.setMessage("Success");
        response.setData(tokenFacade.issueToken(request.getUserId(), LocalDateTime.now()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/token")
    @Operation(summary = "대기열 조회", description = "")
    public ResponseEntity<TokenQueue> getQueueToken(
            @RequestHeader("QueueToken") String token
    ) {
        return new ResponseEntity<>(tokenFacade.getQueueToken(token), HttpStatus.OK);
    }


}//end
