package com.hoonterpark.concertmanager.interfaces.controller.api.response;

import com.hoonterpark.concertmanager.application.result.ConcertResult;

import java.util.List;
import java.util.stream.Collectors;

public class ConcertHttpResponse {

    public record ConcertResponse(
        String name
    ){
        public static List<ConcertResponse> fromResult(List<ConcertResult.Concert> concerts){
            return concerts.stream().map(concert -> new ConcertResponse(concert.concertName())).collect(Collectors.toList());
        }
    }

}
