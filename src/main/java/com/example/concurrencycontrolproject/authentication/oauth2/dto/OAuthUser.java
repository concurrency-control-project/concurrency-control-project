package com.example.concurrencycontrolproject.authentication.oauth2.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.concurrencycontrolproject.domain.user.enums.UserRole;

import lombok.Getter;

@Getter
public class OAuthUser implements OAuth2User {

	private Long id;
	private String email;
	private Collection<? extends GrantedAuthority> authorities;
	private String nickname;
	private String providerId;
	private String providerType;
	private Map<String, Object> attributes;

	public OAuthUser(Long id, String email, UserRole userRole, String nickname, String providerId,
		String providerType) {
		this.id = id;
		this.email = email;
		this.authorities = List.of(new SimpleGrantedAuthority(userRole.name()));
		this.nickname = nickname;
		this.providerId = providerId;
		this.providerType = providerType;
	}

	public OAuthUser(List<SimpleGrantedAuthority> simpleGrantedAuthorities, Map<String, Object> attributes) {
		this.authorities = simpleGrantedAuthorities;
		this.attributes = attributes;
	}

	public Map<String, Object> getAttributes() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", this.id);
		attributes.put("email", this.email);
		attributes.put("role", this.authorities);
		attributes.put("nickname", this.nickname);
		attributes.put("providerId", this.providerId);
		attributes.put("providerType", this.providerType);
		return attributes;
	}

	public UserRole getRole() {
		if (authorities != null && !authorities.isEmpty()) {
			String roleString = authorities.iterator().next().getAuthority();
			return UserRole.valueOf(roleString);
		}
		return UserRole.ROLE_GUEST;
	}

	@Override
	public String getName() {
		return (String)attributes.get("providerId");
	}
}
