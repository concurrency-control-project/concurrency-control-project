package com.example.concurrencycontrolproject.domain.ticket.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;

public interface TicketRepositoryCustom {
	Page<TicketResponse> findTickets(
		Long userId,
		Pageable pageable,
		Long scheduleId,
		String ticketStatus,
		LocalDateTime startedAt,
		LocalDateTime endedAt
	);
}
