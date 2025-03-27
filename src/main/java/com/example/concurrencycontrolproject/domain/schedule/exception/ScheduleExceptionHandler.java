package com.example.concurrencycontrolproject.domain.schedule.exception;

import com.example.concurrencycontrolproject.domain.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.example.concurrencycontrolproject.domain.schedule")
public class ScheduleExceptionHandler {

	@ExceptionHandler(ScheduleException.class)
	public ResponseEntity<ErrorResponse> handleScheduleException(ScheduleException ex) {
		ScheduleErrorCode errorCode = (ScheduleErrorCode) ex.getErrorCode();

		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ErrorResponse.of(errorCode.getCode(), errorCode.getDefaultMessage()));
	}
}
