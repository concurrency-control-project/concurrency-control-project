package com.example.concurrencycontrolproject.domain.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private final HttpStatus status;
	private final String errorCode;
	private final String message;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.status = errorCode.getHttpStatus();
		this.errorCode = errorCode.getCode();
		this.message = errorCode.getDefaultMessage();
	}

	public CustomException(ErrorCode errorCode, String message) {
		super(message);
		this.status = errorCode.getHttpStatus();
		this.errorCode = errorCode.getCode();
		this.message = message;
	}
}
