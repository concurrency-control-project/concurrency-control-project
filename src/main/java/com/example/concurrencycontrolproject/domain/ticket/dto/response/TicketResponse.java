package com.example.concurrencycontrolproject.domain.ticket.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.seat.dto.response.SeatResponseDto;
import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TicketResponse {

	private Long id;
	private Long scheduleId;
	private TicketStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	private SeatResponseDto seat;

	// 티켓 세부 정보
	public static TicketResponse ticketDetailedResponse(
		Ticket ticket,
		SeatResponseDto seatResponseDto
	) {
		return TicketResponse.builder()
			.id(ticket.getId())
			.scheduleId(ticket.getScheduleId())
			.status(ticket.getStatus())
			.createdAt(ticket.getCreatedAt())
			.modifiedAt(ticket.getModifiedAt())
			.seat(seatResponseDto)
			.build();
	}

}
