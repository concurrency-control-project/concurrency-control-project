package com.example.concurrencycontrolproject.auth.exception;

public class AuthenticationExpiredException extends AuthException {
	public AuthenticationExpiredException() {
		super(AuthErrorCode.AUTHENTICATION_EXPIRED);
	}
}
