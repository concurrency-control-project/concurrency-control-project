package com.example.concurrencycontrolproject.domain.auth.exception;

public class AuthException extends RuntimeException {
	AuthException(String message) {
		super(message);
	}
}
