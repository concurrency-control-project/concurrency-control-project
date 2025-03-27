package com.example.concurrencycontrolproject.domain.ticket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketChangeRequest {

	@NotNull(message = "변경할 좌석을 입력해주세요.")
	private Long scheduleSeatId;
}