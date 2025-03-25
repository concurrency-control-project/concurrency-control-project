package com.example.concurrencycontrolproject.domain.seat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.seat.dto.ScheduledSeatRequestDTO;
import com.example.concurrencycontrolproject.domain.seat.service.ScheduledSeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/scheduled-seats")
@RequiredArgsConstructor
public class ScheduledSeatController {
	private final ScheduledSeatService scheduledSeatService;

	@PostMapping("/reserve")
	public ResponseEntity<String> reserveSeat(@RequestBody ScheduledSeatRequestDTO requestDTO) {
		boolean success = scheduledSeatService.reserveSeat(requestDTO.getScheduleId(), requestDTO.getSeatId(), 1L);
		if (success) {
			return ResponseEntity.ok("좌석 예약 성공!");
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("좌석 예약 실패!");
		}
	}

	@PostMapping("/save")
	public ResponseEntity<String> saveToMySQL(@RequestBody ScheduledSeatRequestDTO requestDTO) {
		scheduledSeatService.saveToMySQL(requestDTO.getScheduleId(), requestDTO.getSeatId());
		return ResponseEntity.ok("예약 정보를 MySQL에 저장 완료!");
	}
}

