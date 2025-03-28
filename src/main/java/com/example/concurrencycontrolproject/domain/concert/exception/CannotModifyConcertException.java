package com.example.concurrencycontrolproject.domain.concert.exception;

import com.example.concurrencycontrolproject.domain.common.exception.ConcertException;

public class CannotModifyConcertException extends ConcertException {

	public CannotModifyConcertException() {
		super(ConcertErrorCode.CANNOT_MODIFY_CONCERT);
	}

	public CannotModifyConcertException(String message) {
		super(ConcertErrorCode.CANNOT_MODIFY_CONCERT, message);
	}
}
