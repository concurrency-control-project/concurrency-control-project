package com.example.concurrencycontrolproject.auth.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.example.concurrencycontrolproject.auth.entity.RefreshToken;
import com.example.concurrencycontrolproject.auth.exception.AuthenticationExpiredException;
import com.example.concurrencycontrolproject.auth.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenService {

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
