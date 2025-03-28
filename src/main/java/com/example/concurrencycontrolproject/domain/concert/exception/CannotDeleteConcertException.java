package com.example.concurrencycontrolproject.domain.concert.exception;

import com.example.concurrencycontrolproject.domain.common.exception.ConcertException;

public class CannotDeleteConcertException extends ConcertException {

	public CannotDeleteConcertException() {
		super(ConcertErrorCode.CANNOT_DELETE_CONCERT);
	}

	public CannotDeleteConcertException(String message) {
		super(ConcertErrorCode.CANNOT_DELETE_CONCERT, message);
	}
}

