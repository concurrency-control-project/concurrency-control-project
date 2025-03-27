package com.example.concurrencycontrolproject.domain.seat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatResponseDto {
	private Long id;
	private Integer number;
	private String grade;
	private Integer price;
	private String section;
}
