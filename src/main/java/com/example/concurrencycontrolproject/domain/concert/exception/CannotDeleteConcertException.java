package com.example.concurrencycontrolproject.domain.concert.exception;

import com.example.concurrencycontrolproject.domain.common.exception.CustomException;

public class CannotDeleteConcertException extends CustomException {

	public CannotDeleteConcertException() {
		super(ConcertErrorCode.CANNOT_DELETE_CONCERT);
	}

	public CannotDeleteConcertException(String message) {
		super(ConcertErrorCode.CANNOT_DELETE_CONCERT, message);
	}
}
