package com.example.concurrencycontrolproject.authentication.oauth2.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.authentication.exception.InvalidOauthInfoException;
import com.example.concurrencycontrolproject.authentication.exception.UnsupportedProviderException;
import com.example.concurrencycontrolproject.authentication.oauth2.attribute.OAuth2Attributes;
import com.example.concurrencycontrolproject.authentication.oauth2.dto.OAuthUser;
import com.example.concurrencycontrolproject.authentication.oauth2.info.OAuthUserInfo;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.enums.SocialType;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;

	private static final String NAVER = "naver";
	private static final String KAKAO = "kakao";
	private static final String GITHUB = "github";
	private static final String GOOGLE = "google";

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		SocialType socialType = getSocialType(registrationId);

		String userNameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();
		Map<String, Object> attributes = oAuth2User.getAttributes();

		OAuth2Attributes extractAttributes = OAuth2Attributes.of(socialType, userNameAttributeName, attributes);
		OAuthUser oAuthUser = processOAuthUser(extractAttributes, socialType);

		return new DefaultOAuth2User(
			Collections.singletonList(new SimpleGrantedAuthority(oAuthUser.getRole().name())),
			attributes,
			extractAttributes.getNameAttributeKey()
		);
	}

	private OAuthUser processOAuthUser(OAuth2Attributes attributes, SocialType socialType) {
		OAuthUserInfo oauthUserInfo = attributes.getOauthUserInfo();
		if (oauthUserInfo == null) {
			throw new InvalidOauthInfoException();
		}
		return findOrCreateOAuthUser(oauthUserInfo, socialType);
	}

	private OAuthUser findOrCreateOAuthUser(OAuthUserInfo oauthUserInfo, SocialType socialType) {
		Optional<User> existingUser = userRepository.findByEmailAndSocial(oauthUserInfo.getEmail(), socialType);

		// 첫 로그인 -> GUEST로 User 생성
		User user = existingUser.orElseGet(() -> {
			User newUser = User.builder()
				.email(oauthUserInfo.getEmail())
				.nickname(oauthUserInfo.getNickname())
				.role(UserRole.ROLE_GUEST)
				.build();

			//FIXME: userRepository.save()를 다시 확인해보자.
			//		 AuthService의 addUserInfo에서 새로 User를 생성해 save하고 있다.
			//		 만약 여기서 save하지 못할 경우, 진행이 안 된다면 save유지 -> 진행할 수 있으면 addUserInfo 변경
			return userRepository.save(newUser);
		});

		return new OAuthUser(user.getId(), user.getEmail(), user.getRole(), user.getNickname(),
			oauthUserInfo.getId(), socialType.name());
	}

	private SocialType getSocialType(String registrationId) {
		return switch (registrationId) {
			case NAVER -> SocialType.NAVER;
			case KAKAO -> SocialType.KAKAO;
			case GOOGLE -> SocialType.GOOGLE;
			case GITHUB -> SocialType.GITHUB;
			default -> throw new UnsupportedProviderException();
		};
	}
}
