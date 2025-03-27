package com.example.concurrencycontrolproject.domain.common.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.concurrencycontrolproject.domain.common.response.ValidResponse;

// GlobalExceptionHandler와의 충돌을 피하기 위해 스케줄 도메인 전용 핸들러를 별도로 분리했습니다.
// 해당 예외 처리 로직은 추후 통합할 예정입니다.
@RestControllerAdvice
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
}
