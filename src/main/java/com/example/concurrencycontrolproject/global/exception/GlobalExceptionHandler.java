package com.example.concurrencycontrolproject.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.concurrencycontrolproject.domain.common.response.ErrorResponse;
import com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat.ScheduledSeatException;
import com.example.concurrencycontrolproject.domain.seat.exception.seat.SeatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SeatException.class)
	public ResponseEntity<ErrorResponse> handleSeatException(SeatException e) {
		return ResponseEntity.status(e.getStatus())
			.body(ErrorResponse.of(e.getStatus().toString(), e.getMessage()));
	}

	@ExceptionHandler(ScheduledSeatException.class)
	public ResponseEntity<ErrorResponse> handleScheduledSeatException(ScheduledSeatException e) {
		return ResponseEntity.status(e.getStatus())
			.body(ErrorResponse.of(e.getStatus().toString(), e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
			.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return ResponseEntity.badRequest().body(errors);
	}
}
