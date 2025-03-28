package com.example.concurrencycontrolproject.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.enums.SocialType;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	Optional<User> findByEmailAndSocial(String email, SocialType social);

	boolean existsByEmailAndSocial(String email, SocialType social);
}
