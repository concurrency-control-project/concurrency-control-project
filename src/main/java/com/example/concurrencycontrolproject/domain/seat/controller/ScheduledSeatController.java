package com.example.concurrencycontrolproject.domain.seat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.seat.dto.ScheduledSeatRequestDTO;
import com.example.concurrencycontrolproject.domain.seat.dto.ScheduledSeatResponseDTO;
import com.example.concurrencycontrolproject.domain.seat.entity.ScheduledSeat;
import com.example.concurrencycontrolproject.domain.seat.service.ScheduledSeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("./api/v1/scheduled-seats")
@RequiredArgsConstructor
public class ScheduledSeatController {
	private final ScheduledSeatService scheduledSeatService;

	// 좌석 예약 API
	@PostMapping("./api/v1/scheduledSeat/reserve")
	public ResponseEntity<String> reserveSeat(@RequestBody ScheduledSeatRequestDTO requestDTO) {
		scheduledSeatService.reserveSeat(requestDTO.getScheduleId(), requestDTO.getSeatId(), 1L);
		return ResponseEntity.ok("Reserved Seat!");
	}

	// 예약 취소 API
	@DeleteMapping("./api/v1/scheduledSeat/cancel")
	public ResponseEntity<String> cancelReservation(@RequestBody ScheduledSeatRequestDTO requestDTO) {
		scheduledSeatService.cancelReservation(requestDTO.getScheduleId(), requestDTO.getSeatId());
		return ResponseEntity.ok("Reserved Seat Cancelled!");
	}

	// 예약 상태 조회 API
	@GetMapping("./api/v1/scheduledSeat/{scheduleId}/{seatId}")
	public ResponseEntity<ScheduledSeatResponseDTO> getReservation(@PathVariable Long scheduleId,
		@PathVariable Long seatId) {
		ScheduledSeat reservation = scheduledSeatService.getReservation(scheduleId, seatId);
		ScheduledSeatResponseDTO responseDTO = new ScheduledSeatResponseDTO(
			reservation.getId(), reservation.getScheduleId(), reservation.getSeatId(), reservation.getIsAssigned(),
			reservation.getReservedBy());
		return ResponseEntity.ok(responseDTO);
	}
}


