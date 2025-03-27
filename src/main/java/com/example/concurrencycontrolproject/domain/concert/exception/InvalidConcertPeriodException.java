package com.example.concurrencycontrolproject.domain.concert.exception;

import com.example.concurrencycontrolproject.domain.common.exception.CustomException;

public class InvalidConcertPeriodException extends CustomException {
	public InvalidConcertPeriodException() {
		super(ConcertErrorCode.INVALID_CONCERT_PERIOD);
	}

	public InvalidConcertPeriodException(String message) {
		super(ConcertErrorCode.INVALID_CONCERT_PERIOD, message);
	}
}
