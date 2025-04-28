package com.hoonterpark.concertmanager.application.result;

import com.hoonterpark.concertmanager.domain.enums.SeatStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class ConcertResult {

    public record Concert(
            String concertName
    ) {

    }

    public record ConcertDate(
            LocalDateTime performanceDay
    ) {
    }

    public record ConcertSeat(
            Long seatId,
            String seatNumber,
            SeatStatus seatStatus,
            Long seatPrice
    ) {
    }

}


/*
public final class Concert {
    private final String name;
    private final LocalDateTime date;

    public Concert(String name, LocalDateTime date) {
        this.name = name;
        this.date = date;
    }

    public String name() {
        return name;
    }

    public LocalDateTime date(){
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Concert concert = (Concert) o;
        return name == concert.name
                && Objects.equals(date, concert.date);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, date);
    }

    @Override
    public String toString(){
        return "Concert{"+
                "name='"+name+'\''+
                ", date=" + date + '}';
    }

}
*/