package com.example.concurrencycontrolproject.domain.seat.dto.Seat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatPageResponse {
	private List<SeatResponse> seats;
	private int currentPage;
	private int totalPages;
	private long totalElements;
}

