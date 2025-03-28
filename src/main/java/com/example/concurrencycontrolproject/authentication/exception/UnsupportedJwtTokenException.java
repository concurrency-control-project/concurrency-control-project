package com.example.concurrencycontrolproject.auth.exception;

public class UnsupportedJwtTokenException extends AuthException {
	public UnsupportedJwtTokenException() {
		super(AuthErrorCode.UNSUPPORTED_TOKEN);
	}
}
