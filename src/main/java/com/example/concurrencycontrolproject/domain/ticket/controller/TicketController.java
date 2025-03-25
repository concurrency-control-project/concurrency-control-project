package com.example.concurrencycontrolproject.domain.ticket.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponseDto;
import com.example.concurrencycontrolproject.domain.ticket.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {

	private final TicketService ticketService;

	// 티켓 생성
	@PostMapping("/v1/schedule-seats/{scheduleSeatId}/tickets")
	public ResponseEntity<TicketResponseDto> saveTicket(
		// @Auth AuthUser authUser,
		@RequestParam("userId") Long userId, // 임시
		@PathVariable Long scheduleSeatId
	) {

		return ResponseEntity.ok(ticketService.saveTicket(userId, scheduleSeatId));
	}

	// 티켓 단건 조회
	@GetMapping("/v1/tickets/{ticketId}")
	public ResponseEntity<TicketResponseDto> getTicket(
		// @Auth AuthUser authUser,
		@RequestParam("userId") Long userId, // 임시
		@PathVariable Long ticketId) {
		return ResponseEntity.ok(ticketService.getTicket(userId, ticketId));
	}

	// 티켓 다건 조회
	@GetMapping("/v1/tickets")
	public ResponseEntity<Page<TicketResponseDto>> getTickets(
		// @Auth AuthUser authUser,
		@RequestParam("userId") Long userId, // 임시
		@PageableDefault(page = 1, size = 10, sort = "modifiedAt", direction = DESC) Pageable pageable,
		@RequestParam(required = false) Long concertId,
		@RequestParam(required = false) String scheduleStatus,
		@RequestParam(required = false) String ticketStatus,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startedAt,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endedAt
	) {

		return ResponseEntity.ok(
			ticketService.getTickets(userId, pageable, concertId, scheduleStatus, ticketStatus, startedAt, endedAt));
	}

	// 티켓 취소(환불)

}
