// package com.example.concurrencycontrolproject.global.config.redisson;
//
// import org.redisson.Redisson;
// import org.redisson.api.RedissonClient;
// import org.redisson.config.Config;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// @Configuration
// public class RedissonConfig {
//
// 	@Value("${spring.data.redis.host}")
// 	private String redisHost;
//
// 	@Value("${spring.data.redis.port}")
// 	private int redisPort;
//
// 	private static final String REDIS_PROTOCOL_PREFIX = "redis://";
//
// 	@Bean
// 	public RedissonClient redissonClient() {
// 		Config config = new Config();
//
// 		config.useSingleServer()
// 			.setAddress(REDIS_PROTOCOL_PREFIX + redisHost + ":" + redisPort);
//
// 		return Redisson.create(config);
// 	}
// }