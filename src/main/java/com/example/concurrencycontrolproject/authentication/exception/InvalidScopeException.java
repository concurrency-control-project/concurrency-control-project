package com.example.concurrencycontrolproject.authentication.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

public class InvalidScopeException extends OAuth2AuthenticationException {

	private String message;
	private String scope;

	public InvalidScopeException(String scope) {
		super(OAuth2ErrorCodes.INVALID_SCOPE);
		this.scope = scope;
		this.message = "잘못된 scope: " + scope;
	}
}
