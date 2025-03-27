package com.example.concurrencycontrolproject.domain.concert.exception;

import com.example.concurrencycontrolproject.domain.common.exception.CustomException;

public class InvalidBookingPeriodException extends CustomException {
	public InvalidBookingPeriodException() {
		super(ConcertErrorCode.INVALID_BOOKING_PERIOD);
	}

	public InvalidBookingPeriodException(String message) {
		super(ConcertErrorCode.INVALID_BOOKING_PERIOD, message);
	}
}
