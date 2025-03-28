package com.example.concurrencycontrolproject.authentication.exception;

public class UnsupportedJwtTokenException extends AuthException {
	public UnsupportedJwtTokenException() {
		super(AuthErrorCode.UNSUPPORTED_TOKEN);
	}
}
