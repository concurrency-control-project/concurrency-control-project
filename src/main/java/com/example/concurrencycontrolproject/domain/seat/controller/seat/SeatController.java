package com.example.concurrencycontrolproject.domain.seat.controller.seat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.common.response.PageResponse;
import com.example.concurrencycontrolproject.domain.common.response.Response;
import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatRequestDTO;
import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatResponseDTO;
import com.example.concurrencycontrolproject.domain.seat.service.seat.SeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {
	private final SeatService seatService;

	@PostMapping
	public ResponseEntity<Response<SeatResponseDTO>> createSeat(@RequestBody SeatRequestDTO requestDTO) {
		return ResponseEntity.ok(seatService.createSeat(requestDTO));
	}

	@GetMapping
	public ResponseEntity<Response<PageResponse<SeatResponseDTO>>> getAllSeats(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.ok(seatService.getAllSeats(page, size));
	}

	@GetMapping("/{seatId}")
	public ResponseEntity<Response<SeatResponseDTO>> getSeat(@PathVariable Long seatId) {
		return ResponseEntity.ok(seatService.getSeat(seatId));
	}

	@PutMapping("/{seatId}")
	public ResponseEntity<Response<SeatResponseDTO>> updateSeat(
		@PathVariable Long seatId,
		@RequestBody SeatRequestDTO requestDTO
	) {
		return ResponseEntity.ok(seatService.updateSeat(seatId, requestDTO));
	}

	@DeleteMapping("/{seatId}")
	public ResponseEntity<Response<String>> deleteSeat(@PathVariable Long seatId) {
		return ResponseEntity.ok(seatService.deleteSeat(seatId));
	}
}

