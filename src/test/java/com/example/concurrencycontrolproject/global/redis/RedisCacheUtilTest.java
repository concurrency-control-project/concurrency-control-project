package com.example.concurrencycontrolproject.global.redis;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.concurrencycontrolproject.domain.auth.exception.AuthenticationExpiredException;
import com.example.concurrencycontrolproject.domain.token.entity.RefreshToken;
import com.example.concurrencycontrolproject.domain.token.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class RedisCacheUtilTest {

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@InjectMocks
	private RedisCacheUtil redisCacheUtil;

	RefreshToken refreshToken;
	String token = "token";
	String userId = "1";

	@BeforeEach
	void setUp() {
		refreshToken = new RefreshToken(userId, token);
	}

	@Test
	void saveRefreshToken() {
		String save = redisCacheUtil.saveRefreshToken(token, userId);
		assertThat(save).isEqualTo(token);
	}

	@Test
	void getRefreshToken() {
		given(refreshTokenRepository.findById(anyString())).willReturn(Optional.of(refreshToken));
		String get = redisCacheUtil.getRefreshToken(userId);
		assertThat(get).isEqualTo(token);
	}

	@Test
	void getRefreshToken_잘못된_userId_예외_발생() {
		String badId = "2";
		given(refreshTokenRepository.findById(badId)).willReturn(Optional.empty());
		assertThrows(AuthenticationExpiredException.class, () -> {
			redisCacheUtil.getRefreshToken(badId);
		});
	}

	@Test
	void reissueRefreshToken() {
		String newToken = "newToken";
		String reissue = redisCacheUtil.reissueRefreshToken(newToken, userId);
		assertThat(reissue).isEqualTo(newToken);
		assertThat(reissue).isNotEqualTo(token);
	}
}