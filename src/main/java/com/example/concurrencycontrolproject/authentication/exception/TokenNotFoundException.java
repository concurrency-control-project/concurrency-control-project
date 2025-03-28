package com.example.concurrencycontrolproject.auth.exception;

public class TokenNotFoundException extends AuthException {
	public TokenNotFoundException() {
		super(AuthErrorCode.TOKEN_NOT_FOUND);
	}
}
