package com.example.concurrencycontrolproject.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.example.concurrencycontrolproject.domain.user.enums.UserRole;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = TestSecurityContextFactory.class)
public @interface WithMockAuthUser {
	long userId();

	String email();

	UserRole role();

	String nickname();
}
