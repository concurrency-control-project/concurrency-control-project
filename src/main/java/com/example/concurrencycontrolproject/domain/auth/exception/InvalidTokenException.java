package com.example.concurrencycontrolproject.domain.auth.exception;

public class InvalidTokenException extends AuthException {
	public InvalidTokenException() {
		super(AuthErrorCode.INVALID_TOKEN);
	}
}
