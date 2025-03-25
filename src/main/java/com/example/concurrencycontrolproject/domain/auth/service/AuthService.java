package com.example.concurrencycontrolproject.domain.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.config.JwtUtil;
import com.example.concurrencycontrolproject.domain.auth.dto.SignupResponse;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.exception.AlreadyExistsEmailException;
import com.example.concurrencycontrolproject.domain.user.exception.EmailNotFoundException;
import com.example.concurrencycontrolproject.domain.user.exception.InvalidPasswordException;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtUtil jwtUtil;

	public SignupResponse signup(String email, String password, String nickname, String phoneNumber) {
		if (userRepository.existsByEmail(email)) {
			throw new AlreadyExistsEmailException();
		}

		String encodedPassword = bCryptPasswordEncoder.encode(password);
		User newUser = new User(email, encodedPassword, nickname, phoneNumber);
		userRepository.save(newUser);

		return SignupResponse.from(newUser);
	}

	public void signin(String email, String password, HttpServletResponse servletResponse) {
		User user = userRepository.findByEmail(email).orElseThrow(EmailNotFoundException::new);

		if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
			throw new InvalidPasswordException();
		}

		String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole(),
			user.getNickname());
		String refreshToken = jwtUtil.createRefreshToken(user.getId());

		// 헤더의 Authorization 엑세스토큰 저장
		servletResponse.setHeader("Authorization", accessToken);
		// 쿠키에 리프레시토큰 저장
		setTokenToCookie(refreshToken, servletResponse);
	}

	private void setTokenToCookie(String bearerToken, HttpServletResponse servletResponse) {
		String token = jwtUtil.substringToken(bearerToken);
		Cookie cookie = new Cookie("token", token);
		cookie.setPath("/");
		servletResponse.addCookie(cookie);
	}
}
