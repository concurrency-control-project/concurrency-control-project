package com.example.concurrencycontrolproject.domain.common.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.concurrencycontrolproject.domain.common.response.ErrorResponse;
import com.example.concurrencycontrolproject.domain.common.response.ValidResponse;
import com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat.ScheduledSeatException;
import com.example.concurrencycontrolproject.domain.seat.exception.seat.SeatException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<ValidResponse>> invalidRequestExceptionException(
		MethodArgumentNotValidException ex) {
		List<ValidResponse> errors = new ArrayList<>();

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		for (FieldError err : fieldErrors) {
			errors.add(ValidResponse.of(err.getField(), err.getDefaultMessage()));
		}

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConcertException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(ConcertException e) {
		log.info("CustomException : {}", e.getMessage(), e);
		return new ResponseEntity<>(ErrorResponse.of(e.getErrorCode(), e.getMessage()), e.getStatus());
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ErrorResponse handleGlobalException(Exception e) {
		log.error("Exception : {}", e.getMessage(), e);
		return ErrorResponse.of("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.");
	}

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

	// Todo 충돌 주석
	// @ExceptionHandler(MethodArgumentNotValidException.class)
	// public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
	// 	Map<String, String> errors = new HashMap<>();
	// 	ex.getBindingResult().getFieldErrors()
	// 		.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
	//
	// 	return ResponseEntity.badRequest().body(errors);
	// }

}
