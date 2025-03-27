package com.example.concurrencycontrolproject.global.redis;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;

import com.example.concurrencycontrolproject.domain.auth.exception.AuthenticationExpiredException;
import com.example.concurrencycontrolproject.domain.token.entity.RefreshToken;
import com.example.concurrencycontrolproject.domain.token.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RefreshCacheUtil {

	private final RefreshTokenRepository refreshTokenRepository;

	@Cacheable(value = "refresh", key = "#userId", cacheManager = "redisCache")
	public String saveRefreshToken(String token, String userId) {
		return token;
	}

	@Cacheable(value = "refresh", key = "#userId", cacheManager = "redisCache")
	public String getRefreshToken(String userId) {
		RefreshToken refreshToken = refreshTokenRepository.findById(userId)
			.orElseThrow(AuthenticationExpiredException::new);
		return refreshToken.getToken();
	}

	@CacheEvict(value = "refresh", key = "#userId", cacheManager = "redisCache")
	public void deleteRefreshToken(String userId) {
		refreshTokenRepository.deleteById(userId);
	}

	@CachePut(value = "refresh", key = "#userId", cacheManager = "redisCache")
	public String reissueRefreshToken(String token, String userId) {
		deleteRefreshToken(userId);
		return token;
	}
}
