package com.example.concurrencycontrolproject.domain.concert.exception;

import com.example.concurrencycontrolproject.domain.common.exception.CustomException;

public class CannotModifyConcertException extends CustomException {

	public CannotModifyConcertException() {
		super(ConcertErrorCode.CANNOT_MODIFY_CONCERT);
	}

	public CannotModifyConcertException(String message) {
		super(ConcertErrorCode.CANNOT_MODIFY_CONCERT, message);
	}
}
