package com.example.concurrencycontrolproject.domain.concert.exception;

import com.example.concurrencycontrolproject.domain.common.exception.CustomException;

public class ConcertAlreadyExistsException extends CustomException {

	public ConcertAlreadyExistsException() {
		super(ConcertErrorCode.CONCERT_ALREADY_EXISTS);
	}

	public ConcertAlreadyExistsException(String message) {
		super(ConcertErrorCode.CONCERT_ALREADY_EXISTS, message);
	}
}
