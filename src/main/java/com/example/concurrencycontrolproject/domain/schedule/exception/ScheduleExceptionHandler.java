package com.example.concurrencycontrolproject.domain.schedule.exception;

import com.example.concurrencycontrolproject.domain.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// GlobalExceptionHandler와의 충돌을 피하기 위해 스케줄 도메인 전용 핸들러를 별도로 분리했습니다.
// 해당 예외 처리 로직은 추후 통합할 예정입니다.
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
