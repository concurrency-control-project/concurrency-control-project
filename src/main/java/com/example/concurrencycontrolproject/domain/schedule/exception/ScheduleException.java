package com.example.concurrencycontrolproject.domain.schedule.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ScheduleException extends RuntimeException {
	private final HttpStatus status;
	private final String errorCode;
	private final String message;

	public ScheduleException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.status = errorCode.getHttpStatus();
		this.errorCode = errorCode.getCode();
		this.message = errorCode.getDefaultMessage();
	}

	public ScheduleException(ErrorCode errorCode, String message) {
		super(message);
		this.status = errorCode.getHttpStatus();
		this.errorCode = errorCode.getCode();
		this.message = message;
	}
}
