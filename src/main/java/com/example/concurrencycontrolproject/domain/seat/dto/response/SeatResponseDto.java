package com.example.concurrencycontrolproject.domain.seat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor // querydsl Projections.constructor 때문에 추가 했습니다.
public class SeatResponseDto {
	private Long id;
	private Integer number;
	private String grade;
	private Integer price;
	private String section;
}
