package com.example.concurrencycontrolproject.domain.user.exception;

public class EmailNotFoundException extends UserException {
	public EmailNotFoundException() {
		super(UserErrorCode.EMAIL_NOT_FOUND);
	}
}

