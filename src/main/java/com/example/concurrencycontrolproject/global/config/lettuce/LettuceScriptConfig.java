package com.example.concurrencycontrolproject.global.config.lettuce;

import org.springframework.context.annotation.Configuration;

@Configuration
public class LettuceScriptConfig { // 루아 스크립트를 편리하게 사용하기 위해 레디스스크립트 빈 등록

	// @Bean
	// public RedisScript<Long> lockScript() {
	// 	// 락 루아 스크립트 파일 호출
	// 	DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
	// 	redisScript.setLocation(new ClassPathResource("scripts/lock.lua"));
	// 	// 스크립트의 반환 타입 설정
	// 	redisScript.setResultType(Long.class);
	// 	return redisScript;
	// }
	//
	// @Bean
	// public RedisScript<Long> unlockScript() {
	// 	// 언락 루아 스크립트 파일 호출
	// 	DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
	// 	redisScript.setLocation(new ClassPathResource("scripts/unlock.lua"));
	// 	// 스크립트의 반환 타입 설정
	// 	redisScript.setResultType(Long.class);
	// 	return redisScript;
	// }
}