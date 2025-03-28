package com.example.concurrencycontrolproject.authentication.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

public class UnsupportedProviderException extends OAuth2AuthenticationException {

	public UnsupportedProviderException() {
		super(OAuth2ErrorCodes.UNSUPPORTED_RESPONSE_TYPE);

	}
}
