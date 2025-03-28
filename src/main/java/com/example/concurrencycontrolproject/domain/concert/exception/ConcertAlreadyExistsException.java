package com.example.concurrencycontrolproject.domain.concert.exception;

import com.example.concurrencycontrolproject.domain.common.exception.ConcertException;

public class ConcertAlreadyExistsException extends ConcertException {

	public ConcertAlreadyExistsException() {
		super(ConcertErrorCode.CONCERT_ALREADY_EXISTS);
	}

	public ConcertAlreadyExistsException(String message) {
		super(ConcertErrorCode.CONCERT_ALREADY_EXISTS, message);
	}
}
