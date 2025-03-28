package com.example.concurrencycontrolproject.domain.seat.dto.scheduledSeat;

import com.example.concurrencycontrolproject.domain.seat.entity.scheduledSeat.ScheduledSeat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledSeatResponse {
	private String id;
	private Long scheduleId;
	private Long seatId;
	private Boolean isAssigned;
	private Long reservedBy;

	public ScheduledSeatResponse(ScheduledSeat scheduledSeat) {
		this.id = scheduledSeat.getId();
		this.scheduleId = scheduledSeat.getScheduleId();
		this.seatId = scheduledSeat.getSeatId();
		this.isAssigned = scheduledSeat.getIsAssigned();
		this.reservedBy = scheduledSeat.getReservedBy();
	}
}

