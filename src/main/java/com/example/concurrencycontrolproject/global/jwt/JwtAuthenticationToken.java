package com.example.concurrencycontrolproject.global.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private final AuthUser authUser;

	public JwtAuthenticationToken(AuthUser authUser) {
		super(authUser.getAuthorities());
		this.authUser = authUser;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return authUser;
	}
}
