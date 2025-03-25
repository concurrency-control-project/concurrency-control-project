package com.example.concurrencycontrolproject.domain.auth.exception;

public class TokenNotFoundException extends AuthException {
	public TokenNotFoundException() {
		super("토큰을 찾을 수 없습니다.");
	}
}
