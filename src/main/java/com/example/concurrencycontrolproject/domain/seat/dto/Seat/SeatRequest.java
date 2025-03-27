package com.example.concurrencycontrolproject.domain.seat.dto.Seat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SeatRequest {
	@NotNull(message = "좌석 번호는 필수입니다.")
	private Integer number;
	@NotNull(message = "좌석 등급은 필수입니다.")
	private String grade;
	@NotNull(message = "좌석 가격은 필수입니다.")
	private Integer price;
	@NotNull(message = "좌석 섹션은 필수입니다.")
	private String section;
}
