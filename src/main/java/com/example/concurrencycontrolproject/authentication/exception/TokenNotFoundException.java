package com.example.concurrencycontrolproject.authentication.exception;

public class TokenNotFoundException extends AuthException {
	public TokenNotFoundException() {
		super(AuthErrorCode.TOKEN_NOT_FOUND);
	}
}
