package com.example.concurrencycontrolproject.domain.concert.exception;

import com.example.concurrencycontrolproject.domain.common.exception.CustomException;

public class ConcertNotFoundException extends CustomException {

	public ConcertNotFoundException() {
		super(ConcertErrorCode.CONCERT_NOT_FOUND);
	}

	public ConcertNotFoundException(String message) {
		super(ConcertErrorCode.CONCERT_NOT_FOUND, message);
	}
}
