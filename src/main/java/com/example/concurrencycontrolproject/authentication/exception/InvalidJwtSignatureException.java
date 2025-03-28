package com.example.concurrencycontrolproject.auth.exception;

public class InvalidJwtSignatureException extends AuthException {
	public InvalidJwtSignatureException() {
		super(AuthErrorCode.INVALID_JWT_SIGNATURE);
	}
}
