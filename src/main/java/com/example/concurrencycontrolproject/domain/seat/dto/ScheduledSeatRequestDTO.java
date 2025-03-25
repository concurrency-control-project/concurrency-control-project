package com.example.concurrencycontrolproject.domain.seat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledSeatRequestDTO {
	private Long scheduleId;
	private Long seatId;
}
