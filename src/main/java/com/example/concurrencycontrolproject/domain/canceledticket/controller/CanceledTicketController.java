package com.example.concurrencycontrolproject.domain.canceledticket.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.canceledticket.dto.response.CanceledTicketResponse;
import com.example.concurrencycontrolproject.domain.canceledticket.service.CanceledTicketService;
import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.common.response.Response;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CanceledTicketController {

	private final CanceledTicketService canceledTicketService;

	// 취소된 티켓 다건 조회
	@GetMapping("/v1/canceled-tickets")
	public Response<CanceledTicketResponse> getCanceledTickets(
		@AuthenticationPrincipal AuthUser authUser,
		@PageableDefault(page = 1, size = 10) Pageable pageable,
		@RequestParam(required = false) Long scheduleId,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startedAt,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endedAt
	) {
		return Response.fromPage(
			canceledTicketService.getCanceledTickets(authUser, pageable, scheduleId, startedAt, endedAt)
		);
	}
}