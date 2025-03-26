package com.example.concurrencycontrolproject.domain.user.enums;

import java.util.Arrays;

import com.example.concurrencycontrolproject.domain.user.exception.InvalidUserRoleException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	ROLE_ADMIN(Authority.ADMIN),
	ROLE_USER(Authority.USER);

	private final String userRole;

	public static UserRole of(String role) {
		return Arrays.stream(UserRole.values())
			.filter(r -> r.name().equalsIgnoreCase(role))
			.findFirst()
			.orElseThrow(() -> new InvalidUserRoleException());
	}

	public static class Authority {
		public static final String ADMIN = "ROLE_ADMIN";
		public static final String USER = "ROLE_USER";
	}
}
