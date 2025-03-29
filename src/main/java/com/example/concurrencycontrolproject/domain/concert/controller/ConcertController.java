package com.example.concurrencycontrolproject.domain.concert.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.common.response.Response;
import com.example.concurrencycontrolproject.domain.concert.dto.request.ConcertRequest;
import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertDetailResponse;
import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertResponse;
import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertSearchResponse;
import com.example.concurrencycontrolproject.domain.concert.service.ConcertService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConcertController {

	private final ConcertService concertService;

	@PostMapping("/v1/concerts")
	public Response<ConcertResponse> createConcert(@Valid @RequestBody ConcertRequest concertRequest) {
		return Response.of(concertService.createConcert(concertRequest));
	}

	@GetMapping("/v1/concerts")
	public Response<ConcertSearchResponse> searchConcerts(
		@RequestParam(required = false, defaultValue = "") String keyword,
		Pageable pageable) {
		return Response.fromPage(concertService.searchConcerts(keyword, pageable));
	}

	@GetMapping("/v1/concerts/{concertId}")
	public Response<ConcertDetailResponse> getConcert(@PathVariable Long concertId) {
		return Response.of(concertService.getConcertWithSchedules(concertId));
	}

	@PutMapping("/v1/concerts/{concertId}")
	public Response<Void> updateConcert(@PathVariable Long concertId, @RequestBody ConcertRequest request) {
		concertService.updateConcert(concertId, request);
		return Response.empty();
	}

	@DeleteMapping("/v1/concerts/{concertId}")
	public Response<Void> deleteConcert(@PathVariable Long concertId) {
		concertService.deleteConcert(concertId);
		return Response.empty();
	}
}
