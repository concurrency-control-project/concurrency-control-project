package com.example.concurrencycontrolproject.domain.canceledticket.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.concurrencycontrolproject.domain.canceledticket.dto.response.CanceledTicketResponse;

public interface CanceledTicketRepositoryCustom {
	Page<CanceledTicketResponse> findCanceledTickets(
		Long userId,
		Pageable pageable,
		Long scheduleId,
		LocalDateTime startedAt,
		LocalDateTime endedAt
	);
}