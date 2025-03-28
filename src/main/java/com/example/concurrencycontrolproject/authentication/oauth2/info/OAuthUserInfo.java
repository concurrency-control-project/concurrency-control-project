package com.example.concurrencycontrolproject.authentication.oauth2.info;

import java.util.Map;

public abstract class OAuthUserInfo {

	protected Map<String, Object> attributes;

	public OAuthUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public abstract String getId();

	public abstract String getEmail();

	public abstract String getNickname();

}
