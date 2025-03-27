package com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ScheduledSeatException extends RuntimeException {
	private final ScheduledSeatErrorCode errorCode;
	private HttpStatus status;

	public ScheduledSeatException(ScheduledSeatErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.errorCode = errorCode;
		this.status = errorCode.getHttpStatus();
	}

}
