package com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ScheduledSeatException extends RuntimeException {
	private final ScheduledSeatErrorCode errorCode;
	private HttpStatus status;

	public ScheduledSeatException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.errorCode = errorCode;
		this.status = errorCode.getHttpStatus();
	}

}
