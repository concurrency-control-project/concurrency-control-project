package com.example.concurrencycontrolproject.authentication.exception;

public class OnlyGuestUserException extends AuthException {
	public OnlyGuestUserException() {
		super(AuthErrorCode.ONLY_GUEST_USER);
	}
}
