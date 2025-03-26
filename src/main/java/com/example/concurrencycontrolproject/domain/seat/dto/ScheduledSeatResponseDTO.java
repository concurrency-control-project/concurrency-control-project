package com.example.concurrencycontrolproject.domain.seat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledSeatResponseDTO {
	private String id;
	private Long scheduleId;
	private Long seatId;
	private Boolean isAssigned;
	private Long reservedBy;
}

