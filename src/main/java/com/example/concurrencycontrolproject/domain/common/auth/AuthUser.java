package com.example.concurrencycontrolproject.domain.common.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.concurrencycontrolproject.domain.user.enums.UserRole;

import lombok.Getter;

@Getter
public class AuthUser {

	private final Long id;
	private final String email;
	private final Collection<? extends GrantedAuthority> authorities;
	private final String nickname;

	public AuthUser(Long id, String email, UserRole userRole, String nickname) {
		this.id = id;
		this.email = email;
		this.authorities = List.of(new SimpleGrantedAuthority(userRole.name()));
		this.nickname = nickname;
	}
}
