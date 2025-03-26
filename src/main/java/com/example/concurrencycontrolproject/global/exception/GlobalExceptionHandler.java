package com.example.concurrencycontrolproject.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat.ScheduledSeatException;
import com.example.concurrencycontrolproject.domain.seat.exception.seat.SeatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SeatException.class)
	public ResponseEntity<String> handleSeatException(SeatException e) {
		return ResponseEntity.status(e.getStatus()).body(e.getMessage());
	}

	@ExceptionHandler(ScheduledSeatException.class)
	public ResponseEntity<String> handleScheduledSeatException(ScheduledSeatException e) {
		return ResponseEntity.status(e.getStatus()).body(e.getMessage());
	}
}
