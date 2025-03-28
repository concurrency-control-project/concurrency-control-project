package com.example.concurrencycontrolproject.authentication.oauth2.attribute;

import java.util.EnumSet;
import java.util.Map;

import com.example.concurrencycontrolproject.authentication.exception.UnsupportedProviderException;
import com.example.concurrencycontrolproject.authentication.oauth2.info.GithubOAuthUserInfo;
import com.example.concurrencycontrolproject.authentication.oauth2.info.GoogleOAuthUserInfo;
import com.example.concurrencycontrolproject.authentication.oauth2.info.KakaoOAuthUserInfo;
import com.example.concurrencycontrolproject.authentication.oauth2.info.NaverOAuthUserInfo;
import com.example.concurrencycontrolproject.authentication.oauth2.info.OAuthUserInfo;
import com.example.concurrencycontrolproject.domain.user.enums.SocialType;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuth2Attributes {

	private OAuthUserInfo oauthUserInfo;
	private String nameAttributeKey;
	private UserRole role;

	@Builder
	private OAuth2Attributes(String nameAttributeKey, OAuthUserInfo oauthUserInfo) {
		this.nameAttributeKey = nameAttributeKey;
		this.oauthUserInfo = oauthUserInfo;
		this.role = UserRole.ROLE_GUEST;
	}

	public static OAuth2Attributes of(SocialType socialType, String userNameAttributeName,
		Map<String, Object> attributes) {

		if (!EnumSet.allOf(SocialType.class).contains(socialType)) {
			throw new UnsupportedProviderException();
		}

		return switch (socialType) {
			case GOOGLE -> ofGoogle(userNameAttributeName, attributes);
			case GITHUB -> ofGithub(userNameAttributeName, attributes);
			case NAVER -> ofNaver(userNameAttributeName, attributes);
			case KAKAO -> ofKakao(userNameAttributeName, attributes);
			case NONE -> throw new UnsupportedProviderException();
		};
	}

	private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuth2Attributes.builder()
			.nameAttributeKey(userNameAttributeName)
			.oauthUserInfo(new GoogleOAuthUserInfo(attributes))
			.build();
	}

	private static OAuth2Attributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuth2Attributes.builder()
			.nameAttributeKey(userNameAttributeName)
			.oauthUserInfo(new GithubOAuthUserInfo(attributes))
			.build();
	}

	private static OAuth2Attributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuth2Attributes.builder()
			.nameAttributeKey(userNameAttributeName)
			.oauthUserInfo(new NaverOAuthUserInfo(attributes))
			.build();
	}

	private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuth2Attributes.builder()
			.nameAttributeKey(userNameAttributeName)
			.oauthUserInfo(new KakaoOAuthUserInfo(attributes))
			.build();
	}
}
