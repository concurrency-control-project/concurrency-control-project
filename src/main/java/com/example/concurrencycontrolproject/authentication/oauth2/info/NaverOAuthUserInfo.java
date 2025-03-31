package com.example.concurrencycontrolproject.authentication.oauth2.info;

import java.util.Map;

import com.example.concurrencycontrolproject.authentication.exception.InvalidScopeException;

public class NaverOAuthUserInfo extends OAuthUserInfo {

	public NaverOAuthUserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return (String)getResponse().get("id");
	}

	@Override
	public String getEmail() {
		return (String)getResponse().get("email");
	}

	@Override
	public String getNickname() {
		return (String)getResponse().get("nickname");
	}

	private Map<String, Object> getResponse() {
		Map<String, Object> response = (Map<String, Object>)attributes.get("response");
		if (response == null) {
			throw new InvalidScopeException("response");
		}
		return response;
	}
}
