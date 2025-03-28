package com.example.concurrencycontrolproject.domain.seat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.example.concurrencycontrolproject.domain.seat.entity.scheduledSeat")
public class RedisSeatRepositoryConfig {
}
