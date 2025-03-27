package com.example.concurrencycontrolproject.domain.seat.dto.Seat;

import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;

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

	public static SeatResponse fromSeat(Seat seat) {
		SeatResponse seatResponse = new SeatResponse();
		seatResponse.setId(seat.getId());
		seatResponse.setNumber(seat.getNumber());
		seatResponse.setGrade(seat.getGrade());
		seatResponse.setPrice(seat.getPrice());
		seatResponse.setSection(seat.getSection());
		return seatResponse;
	}
}
