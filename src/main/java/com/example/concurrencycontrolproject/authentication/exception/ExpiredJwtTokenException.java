package com.example.concurrencycontrolproject.auth.exception;

public class ExpiredJwtTokenException extends AuthException {
	public ExpiredJwtTokenException() {
		super(AuthErrorCode.TOKEN_EXPIRED);
	}
}
