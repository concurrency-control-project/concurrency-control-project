package com.example.concurrencycontrolproject.domain.auth.exception;

public class ExpiredJwtTokenException extends AuthException {
	public ExpiredJwtTokenException() {
		super(AuthErrorCode.TOKEN_EXPIRED);
	}
}
