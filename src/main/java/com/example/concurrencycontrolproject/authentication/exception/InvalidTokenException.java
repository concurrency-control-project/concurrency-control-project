package com.example.concurrencycontrolproject.authentication.exception;

public class InvalidTokenException extends AuthException {
	public InvalidTokenException() {
		super(AuthErrorCode.INVALID_TOKEN);
	}
}
