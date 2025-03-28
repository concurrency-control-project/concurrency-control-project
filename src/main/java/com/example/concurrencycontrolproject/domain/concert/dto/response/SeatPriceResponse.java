package com.example.concurrencycontrolproject.domain.concert.dto.response;

import java.math.BigDecimal;

import lombok.Getter;

//TODO 패키지 이동
@Getter
public class SeatPriceResponse {

	private final String seatGrade;
	private final BigDecimal price;

	private SeatPriceResponse(String seatGrade, BigDecimal price) {
		this.seatGrade = seatGrade;
		this.price = price;
	}

	//실제값 넣기
	public static SeatPriceResponse of(String seatGrade, BigDecimal price) {
		return new SeatPriceResponse("", BigDecimal.ZERO);
	}
}
