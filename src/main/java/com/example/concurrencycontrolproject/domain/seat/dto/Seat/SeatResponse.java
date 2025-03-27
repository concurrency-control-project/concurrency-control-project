package com.example.concurrencycontrolproject.domain.seat.dto.Seat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
	private Long id;
	private Integer number;
	private String grade;
	private Integer price;
	private String section;
}
