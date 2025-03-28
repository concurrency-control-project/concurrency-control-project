package com.example.concurrencycontrolproject.authentication.oauth2.info;

import java.util.Map;

public class GoogleOAuthUserInfo extends OAuthUserInfo {

	public GoogleOAuthUserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return (String)attributes.get("sub");
	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public String getNickname() {
		return (String)attributes.get("given_name");
	}
}
