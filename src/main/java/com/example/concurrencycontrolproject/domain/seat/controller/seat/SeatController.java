package com.example.concurrencycontrolproject.domain.seat.controller.seat;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatRequestDTO;
import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatResponseDTO;
import com.example.concurrencycontrolproject.domain.seat.service.seat.SeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("./api/v1/seats")
@RequiredArgsConstructor
public class SeatController {
	private final SeatService seatService;

	@PostMapping
	public ResponseEntity<SeatResponseDTO> createSeat(@RequestBody SeatRequestDTO requestDTO) {
		return ResponseEntity.ok(seatService.createSeat(requestDTO));
	}

	@GetMapping("./api/v1/seats/getAll")
	public ResponseEntity<List<SeatResponseDTO>> getAllSeats() {
		return ResponseEntity.ok(seatService.getAllSeats());
	}
}

