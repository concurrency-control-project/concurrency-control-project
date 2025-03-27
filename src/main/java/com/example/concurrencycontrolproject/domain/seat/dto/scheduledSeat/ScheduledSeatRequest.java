package com.example.concurrencycontrolproject.domain.seat.dto.scheduledSeat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledSeatRequest {
	private Long scheduleId;
	private Long seatId;
}
