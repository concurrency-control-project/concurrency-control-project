package com.example.concurrencycontrolproject.domain.user.exception;

public class EmailAccessDeniedException extends UserException {
	public EmailAccessDeniedException() {
		super(UserErrorCode.EMAIL_ACCESS_DENIED);
	}
}
