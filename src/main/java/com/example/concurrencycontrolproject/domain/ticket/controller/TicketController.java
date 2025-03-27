package com.example.concurrencycontrolproject.domain.ticket.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.common.response.Response;
import com.example.concurrencycontrolproject.domain.ticket.dto.request.TicketChangeRequest;
import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;
import com.example.concurrencycontrolproject.domain.ticket.service.TicketService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TicketController {

	private final TicketService ticketService;

	// 티켓 생성
	@PostMapping("/v1/schedule-seats/{scheduleSeatId}/tickets")
	public Response<TicketResponse> saveTicket(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long scheduleSeatId
	) {

		return Response.of(ticketService.saveTicket(authUser, scheduleSeatId));
	}

	// 티켓 단건 조회
	@GetMapping("/v1/tickets/{ticketId}")
	public Response<TicketResponse> getTicket(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long ticketId
	) {
		return Response.of(ticketService.getTicket(authUser, ticketId));
	}

	// 티켓 다건 조회
	@GetMapping("/v1/tickets")
	public Response<TicketResponse> getTickets(
		@AuthenticationPrincipal AuthUser authUser,
		@PageableDefault(page = 1, size = 10, sort = "createdAt", direction = DESC) Pageable pageable,
		@RequestParam(required = false) Long scheduleId,
		@RequestParam(required = false) String ticketStatus,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startedAt,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endedAt
	) {

		return Response.fromPage(
			ticketService.getTickets(authUser, pageable, scheduleId, ticketStatus, startedAt,
				endedAt));
	}

	// 티켓 취소(환불)
	@DeleteMapping("/v1/tickets/{ticketId}")
	public Response<Void> deleteTicket(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long ticketId
	) {
		ticketService.deleteTicket(authUser, ticketId);
		return Response.empty();
	}

	// 좌석 변경
	@PutMapping("/v1/tickets/{ticketId}")
	public ResponseEntity<TicketResponse> updateTicket(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody TicketChangeRequest requestDto,
		@PathVariable Long ticketId
	) {
		return ResponseEntity.ok(ticketService.updateTicket(authUser, ticketId, requestDto));
	}
}
