package com.example.concurrencycontrolproject.domain.seat.exception.seat;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class SeatException extends RuntimeException {
	private final HttpStatus status;

	public SeatException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.status = errorCode.getHttpStatus();
	}
}

