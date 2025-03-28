package com.example.concurrencycontrolproject.authentication.auth.service;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.authentication.auth.dto.AdditionalInfoRequest;
import com.example.concurrencycontrolproject.authentication.auth.dto.SignupResponse;
import com.example.concurrencycontrolproject.authentication.auth.dto.UserProfileResponse;
import com.example.concurrencycontrolproject.authentication.exception.DuplicateSocialEmailException;
import com.example.concurrencycontrolproject.authentication.exception.UnsupportedProviderException;
import com.example.concurrencycontrolproject.authentication.jwt.service.RefreshTokenService;
import com.example.concurrencycontrolproject.authentication.jwt.util.JwtUtil;
import com.example.concurrencycontrolproject.authentication.oauth2.dto.OAuthUser;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.enums.SocialType;
import com.example.concurrencycontrolproject.domain.user.exception.AlreadyExistsEmailException;
import com.example.concurrencycontrolproject.domain.user.exception.EmailAccessDeniedException;
import com.example.concurrencycontrolproject.domain.user.exception.EmailNotFoundException;
import com.example.concurrencycontrolproject.domain.user.exception.InvalidPasswordException;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenService redisCache;

	public SignupResponse signup(String email, String password, String nickname, String phoneNumber) {
		if (userRepository.existsByEmail(email)) {
			throw new AlreadyExistsEmailException();
		}

		String encodedPassword = bCryptPasswordEncoder.encode(password);
		User user = new User(email, encodedPassword, nickname, phoneNumber);
		User saveUser = userRepository.save(user);

		return SignupResponse.from(saveUser);
	}

	public void signin(String email, String password, HttpServletResponse servletResponse) {
		User user = userRepository.findByEmail(email).orElseThrow(EmailNotFoundException::new);
		Optional.ofNullable(user.getDeletedAt())
			.filter(Objects::nonNull)
			.ifPresent(deletedAt -> {
				throw new EmailAccessDeniedException();
			});

		if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
			throw new InvalidPasswordException();
		}

		createAndSaveJwt(user, servletResponse);
	}

	public UserProfileResponse addUserInfo(OAuthUser oAuthUser, AdditionalInfoRequest request) {
		String email = oAuthUser.getEmail();
		String nickname = oAuthUser.getNickname();
		SocialType social = getSocialType(oAuthUser.getProviderType());
		String password = request.getPassword();
		String phoneNumber = request.getPhoneNumber();

		if (userRepository.existsByEmailAndSocial(email, social)) {
			throw new DuplicateSocialEmailException();
		}

		String encodedPassword = bCryptPasswordEncoder.encode(password);
		User user = new User(email, encodedPassword, nickname, phoneNumber, social);
		User saveUser = userRepository.save(user);

		return UserProfileResponse.from(saveUser);
	}

	private void createAndSaveJwt(User user, HttpServletResponse servletResponse) {
		String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole(),
			user.getNickname());
		jwtUtil.accessTokenSetHeader(accessToken, servletResponse);

		String refreshToken = jwtUtil.createRefreshToken(user.getId());
		jwtUtil.refreshTokenSetCookie(refreshToken, servletResponse);
		redisCache.saveRefreshToken(refreshToken, String.valueOf(user.getId()));
	}

	private SocialType getSocialType(String social) {
		return EnumSet.allOf(SocialType.class).stream()
			.filter(e -> e.name().equalsIgnoreCase(social))
			.findFirst()
			.orElseThrow(UnsupportedProviderException::new);
	}
}
