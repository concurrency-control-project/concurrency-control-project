package com.example.concurrencycontrolproject.domain.auth.exception;

public class UnsupportedJwtTokenException extends AuthException {
	public UnsupportedJwtTokenException() {
		super(AuthErrorCode.UNSUPPORTED_TOKEN);
	}
}
