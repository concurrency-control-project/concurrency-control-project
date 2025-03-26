package com.example.concurrencycontrolproject.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.concurrencycontrolproject.domain.seat.exception.ScheduledSeatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ScheduledSeatException.class)
	public ResponseEntity<String> handleScheduledSeatException(ScheduledSeatException e) {
		return ResponseEntity.status(e.getStatus()).body(e.getMessage());
	}
}
