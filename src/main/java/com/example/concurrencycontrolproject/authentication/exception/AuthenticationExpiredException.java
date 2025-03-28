package com.example.concurrencycontrolproject.authentication.exception;

public class AuthenticationExpiredException extends AuthException {
	public AuthenticationExpiredException() {
		super(AuthErrorCode.AUTHENTICATION_EXPIRED);
	}
}
