package com.hoonterpark.concertmanager.presentation;

import com.hoonterpark.concertmanager.application.UserFacade;
import com.hoonterpark.concertmanager.presentation.controller.request.ChargeBalanceRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.UserBalanceResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "유저 컨트롤러", description = "유저 포인트 충전, 유저 포인트 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserFacade userFacade;

    @PatchMapping("/balance/charge")
    public ResponseEntity<UserBalanceResponse> chargeBalance(
            @RequestBody ChargeBalanceRequest request
    ) {
        return new ResponseEntity<>(userFacade.chargeUserPoint(request.getUserId(), request.getAmount()), HttpStatus.OK);
    }


    @GetMapping("/balance")
    public ResponseEntity<UserBalanceResponse> getBalance(
            @RequestParam Long userId
    ) {
        return new ResponseEntity<>(userFacade.getUserBalance(userId), HttpStatus.OK);
    }
}
