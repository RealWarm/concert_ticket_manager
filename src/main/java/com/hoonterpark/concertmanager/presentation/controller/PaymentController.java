package com.hoonterpark.concertmanager.presentation.controller;


import com.hoonterpark.concertmanager.application.PaymentFacade;
import com.hoonterpark.concertmanager.presentation.controller.request.PaymentRequest;
import com.hoonterpark.concertmanager.presentation.controller.response.CommonResponse;
import com.hoonterpark.concertmanager.presentation.controller.response.PaymentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "결제 컨트롤러", description = "결제 생성")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final PaymentFacade paymentFacade;

    @PostMapping("/pay")
    public ResponseEntity<CommonResponse<PaymentResponse>> pay(
            @RequestHeader("Authorization") String token,
            @RequestBody PaymentRequest request
    ) {
        CommonResponse<PaymentResponse> response = new CommonResponse<>();
        response.setResult("200");
        response.setMessage("Success");
        response.setData(paymentFacade.makePayment(request, token, LocalDateTime.now()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
