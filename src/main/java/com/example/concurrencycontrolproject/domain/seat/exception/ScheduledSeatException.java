package com.example.concurrencycontrolproject.domain.seat.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ScheduledSeatException extends RuntimeException {
	private HttpStatus status;

	public ScheduledSeatException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.status = errorCode.getHttpStatus();
	}
}
