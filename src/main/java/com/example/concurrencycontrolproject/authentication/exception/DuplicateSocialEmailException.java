package com.example.concurrencycontrolproject.authentication.exception;

public class DuplicateSocialEmailException extends AuthException {
	public DuplicateSocialEmailException() {
		super(AuthErrorCode.DUPLICATE_SOCIAL_EMAIL);
	}
}
