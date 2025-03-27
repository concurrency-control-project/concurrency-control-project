package com.example.concurrencycontrolproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisConfig {
	@Bean
	public DefaultRedisScript<Long> redisScript() {
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/reserve_seat.lua")));
		redisScript.setResultType(Long.class);
		return redisScript;
	}
}
