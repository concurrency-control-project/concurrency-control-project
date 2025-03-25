package com.example.concurrencycontrolproject.domain.token.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.concurrencycontrolproject.domain.token.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
	
}
