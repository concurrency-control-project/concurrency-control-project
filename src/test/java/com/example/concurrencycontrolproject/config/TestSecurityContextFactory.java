package com.example.concurrencycontrolproject.config;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.global.jwt.JwtAuthenticationToken;

public class TestSecurityContextFactory implements WithSecurityContextFactory<WithMockAuthUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockAuthUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		AuthUser authUser = new AuthUser(customUser.userId(), customUser.email(), customUser.role(),
			customUser.nickname());
		JwtAuthenticationToken authentication = new JwtAuthenticationToken(authUser);

		context.setAuthentication(authentication);
		return context;
	}
}
