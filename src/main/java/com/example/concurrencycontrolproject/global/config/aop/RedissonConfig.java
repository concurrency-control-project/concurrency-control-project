package com.example.concurrencycontrolproject.global.config.aop;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig { // Todo 튜터님께서 어차피 자동 생성 된다고 넣지 말라고 하셨는 데, 이거 없으면 자꾸 redissonClient 주입 오류 떠서 임시로 만들었습니다

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	private static final String REDIS_PROTOCOL_PREFIX = "redis://";

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();

		config.useSingleServer()
			.setAddress(REDIS_PROTOCOL_PREFIX + redisHost + ":" + redisPort);

		return Redisson.create(config);
	}
}
