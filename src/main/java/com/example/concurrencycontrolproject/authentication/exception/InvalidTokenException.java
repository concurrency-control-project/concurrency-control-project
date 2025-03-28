package com.example.concurrencycontrolproject.auth.exception;

public class InvalidTokenException extends AuthException {
	public InvalidTokenException() {
		super(AuthErrorCode.INVALID_TOKEN);
	}
}
