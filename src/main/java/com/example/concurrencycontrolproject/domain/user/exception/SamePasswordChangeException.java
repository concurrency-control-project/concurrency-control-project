package com.example.concurrencycontrolproject.domain.user.exception;

public class SamePasswordChangeException extends UserException {
	public SamePasswordChangeException() {
		super(UserErrorCode.SAME_PASSWORD_CHANGE);
	}
}
