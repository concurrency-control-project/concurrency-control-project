package com.example.concurrencycontrolproject.domain.seat.controller.scheduledSeat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.common.response.Response;
import com.example.concurrencycontrolproject.domain.seat.dto.scheduledSeat.ScheduledSeatRequestDTO;
import com.example.concurrencycontrolproject.domain.seat.dto.scheduledSeat.ScheduledSeatResponseDTO;
import com.example.concurrencycontrolproject.domain.seat.service.scheduledSeat.ScheduledSeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/scheduled-seats")
@RequiredArgsConstructor
public class ScheduledSeatController {
	private final ScheduledSeatService scheduledSeatService;

	// 좌석 예약 API
	@PostMapping("v1/scheduled-seats")
	public ResponseEntity<Response<String>> reserveSeat(@RequestBody ScheduledSeatRequestDTO requestDTO) {
		return ResponseEntity.ok(
			scheduledSeatService.reserveSeat(requestDTO.getScheduleId(), requestDTO.getSeatId(), 1L));
	}

	// 예약 취소 API
	@DeleteMapping("v1/scheduled-seat/{scheduleId}/{seatId}")
	public ResponseEntity<Response<String>> cancelReservation(@RequestBody ScheduledSeatRequestDTO requestDTO) {
		return ResponseEntity.ok(
			scheduledSeatService.cancelReservation(requestDTO.getScheduleId(), requestDTO.getSeatId()));
	}

	// 예약 상태 조회 API
	@GetMapping("v1/scheduled-seats/{scheduleId}/{seatId}")
	public ResponseEntity<Response<ScheduledSeatResponseDTO>> getReservation(@PathVariable Long scheduleId,
		@PathVariable Long seatId) {
		return ResponseEntity.ok(scheduledSeatService.getReservation(scheduleId, seatId));
	}
}


