package com.example.concurrencycontrolproject.domain.auth.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.auth.dto.SignupResponse;
import com.example.concurrencycontrolproject.domain.token.repository.RefreshTokenRepository;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.exception.AlreadyExistsEmailException;
import com.example.concurrencycontrolproject.domain.user.exception.EmailAccessDeniedException;
import com.example.concurrencycontrolproject.domain.user.exception.EmailNotFoundException;
import com.example.concurrencycontrolproject.domain.user.exception.InvalidPasswordException;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;
import com.example.concurrencycontrolproject.global.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	public SignupResponse signup(String email, String password, String nickname, String phoneNumber) {
		if (userRepository.existsByEmail(email)) {
			throw new AlreadyExistsEmailException();
		}

		String encodedPassword = bCryptPasswordEncoder.encode(password);
		User User = new User(email, encodedPassword, nickname, phoneNumber);
		User saveUser = userRepository.save(User);

		return SignupResponse.from(saveUser);
	}

	public void signin(String email, String password, HttpServletResponse servletResponse) {
		User user = userRepository.findByEmail(email).orElseThrow(EmailNotFoundException::new);
		Optional.ofNullable(user.getDeletedAt())
			.filter(Objects::nonNull)
			.ifPresent(deletedAt -> {
				throw new EmailAccessDeniedException();
			});

		if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
			throw new InvalidPasswordException();
		}

		jwtUtil.saveAccessToken(
			jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole(), user.getNickname()),
			servletResponse);
		jwtUtil.saveRefreshToken(
			jwtUtil.createRefreshToken(user.getId()), String.valueOf(user.getId()), servletResponse);
	}
}
