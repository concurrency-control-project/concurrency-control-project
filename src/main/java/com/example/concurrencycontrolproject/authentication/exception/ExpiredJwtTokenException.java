package com.example.concurrencycontrolproject.authentication.exception;

public class ExpiredJwtTokenException extends AuthException {
	public ExpiredJwtTokenException() {
		super(AuthErrorCode.TOKEN_EXPIRED);
	}
}
