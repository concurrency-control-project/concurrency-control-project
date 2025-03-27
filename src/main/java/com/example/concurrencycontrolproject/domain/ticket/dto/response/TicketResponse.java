package com.example.concurrencycontrolproject.domain.ticket.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.seat.dto.response.SeatResponse;
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
	private SeatResponse seat;

	public static TicketResponse ticketResponse(Ticket ticket) {
		return TicketResponse.builder()
			.id(ticket.getId())
			.scheduleId(ticket.getScheduleSeat().getSchedule().getId())
			.status(ticket.getStatus())
			.createdAt(ticket.getCreatedAt())
			.modifiedAt(ticket.getModifiedAt())
			.seat(SeatResponse.builder()  // SeatResponse 생성
				.id(ticket.getScheduleSeat().getSeat().getId())
				.number(ticket.getScheduleSeat().getSeat().getNumber())
				.grade(ticket.getScheduleSeat().getSeat().getGrade())
				.price(ticket.getScheduleSeat().getSeat().getPrice())
				.section(ticket.getScheduleSeat().getSeat().getSection())
				.build())
			.build();
	}

}
