package com.example.concurrencycontrolproject.domain.token.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash(value = "refresh_token", timeToLive = 21600)
public class RefreshToken {

	@Id
	private String userId;
	private String token;

	public RefreshToken(String userId, String token) {
		this.userId = userId;
		this.token = token;
	}

}
