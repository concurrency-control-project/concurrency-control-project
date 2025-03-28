package com.example.concurrencycontrolproject.authentication.oauth2.info;

import java.util.Map;

import com.example.concurrencycontrolproject.authentication.exception.InvalidScopeException;

public class KakaoOAuthUserInfo extends OAuthUserInfo {

	public KakaoOAuthUserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return String.valueOf(attributes.get("id"));
	}

	@Override
	public String getEmail() {
		return (String)getAccount().get("email");
	}

	@Override
	public String getNickname() {
		Map<String, Object> account = getAccount();
		return (String)getProfile(account).get("nickname");
	}

	private Map<String, Object> getAccount() {
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		if (account == null) {
			throw new InvalidScopeException("kakao_account");
		}
		return account;
	}

	private Map<String, Object> getProfile(Map<String, Object> account) {
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");
		if (profile == null) {
			throw new InvalidScopeException("profile");
		}
		return profile;
	}
}