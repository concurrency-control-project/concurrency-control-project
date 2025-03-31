package com.example.concurrencycontrolproject.domain.seat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponseDto {
	private Long id;
	private Integer number;
	private String grade;
	private Integer price;
	private String section;
}
