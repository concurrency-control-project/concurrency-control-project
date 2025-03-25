package com.example.concurrencycontrolproject.domain.ticket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketChangeRequestDto {

	@NotNull
	private Long seatId;
}
