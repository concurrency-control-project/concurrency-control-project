package com.example.concurrencycontrolproject.domain.seat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.seat.dto.SeatRequestDTO;
import com.example.concurrencycontrolproject.domain.seat.dto.SeatResponseDTO;
import com.example.concurrencycontrolproject.domain.seat.service.SeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatController {
	private final SeatService seatService;

	@PostMapping
	public ResponseEntity<SeatResponseDTO> createSeat(@RequestBody SeatRequestDTO requestDTO) {
		return ResponseEntity.ok(seatService.createSeat(requestDTO));
	}

	@GetMapping
	public ResponseEntity<List<SeatResponseDTO>> getAllSeats() {
		return ResponseEntity.ok(seatService.getAllSeats());
	}
}

