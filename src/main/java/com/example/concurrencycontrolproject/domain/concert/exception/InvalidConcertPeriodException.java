package com.example.concurrencycontrolproject.domain.concert.exception;

import com.example.concurrencycontrolproject.domain.common.exception.ConcertException;

public class InvalidConcertPeriodException extends ConcertException {
	public InvalidConcertPeriodException() {
		super(ConcertErrorCode.INVALID_CONCERT_PERIOD);
	}

	public InvalidConcertPeriodException(String message) {
		super(ConcertErrorCode.INVALID_CONCERT_PERIOD, message);
	}
}
