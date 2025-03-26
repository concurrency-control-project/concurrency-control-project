package com.example.concurrencycontrolproject.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.concurrencycontrolproject.domain.auth.dto.SignupResponse;
import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;
import com.example.concurrencycontrolproject.domain.user.exception.AlreadyExistsEmailException;
import com.example.concurrencycontrolproject.domain.user.exception.EmailNotFoundException;
import com.example.concurrencycontrolproject.domain.user.exception.InvalidPasswordException;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;
import com.example.concurrencycontrolproject.global.jwt.JwtUtil;
import com.example.concurrencycontrolproject.global.redis.RedisCacheUtil;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private RedisCacheUtil redisCacheUtil;
	@Mock
	private JwtUtil jwtUtil;
	@Mock
	private UserRepository userRepository;
	@Spy
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	User user;
	AuthUser authUser;
	Long userId = 1L;
	String email = "email@email.com";
	String password = "Password!123456";
	String nickname = "nickname";
	String phoneNumber = "010-0000-0000";
	String encodedPassword;

	@BeforeEach
	void setUp() {
		encodedPassword = passwordEncoder.encode(password);

		user = new User(email, encodedPassword, nickname, phoneNumber);
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(user, "modifiedAt", LocalDateTime.now());

		authUser = new AuthUser(userId, email, UserRole.ROLE_USER, nickname);
	}

	@Test
	void signup() {
		given(userRepository.existsByEmail(anyString())).willReturn(false);
		given(passwordEncoder.encode(anyString())).willReturn(password);
		given(userRepository.save(any())).willReturn(user);

		SignupResponse signup = authService.signup(email, encodedPassword, nickname, phoneNumber);

		assertThat(signup).isNotNull();
		assertThat(signup.getId()).isEqualTo(userId);
	}

	@Test
	void signup_중복된_email로_회원가입_시_예외_발생() {
		given(userRepository.existsByEmail(anyString())).willReturn(true);

		assertThrows(AlreadyExistsEmailException.class, () -> {
			authService.signup(email, password, nickname, phoneNumber);
		});
	}

	@Test
	void signin() {
		HttpServletResponse response = new MockHttpServletResponse();

		given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

		authService.signin(email, password, response);

		assertThat(user.getEmail()).isEqualTo(email);
	}

	@Test
	void signin_찾을_수_없는_이메일로_로그인() {
		String badEmail = "badEmail@email.com";
		HttpServletResponse response = new MockHttpServletResponse();

		given(userRepository.findByEmail(badEmail)).willReturn(Optional.empty());

		assertThat(user.getEmail()).isNotEqualTo(badEmail);
		assertThrows(EmailNotFoundException.class, () -> {
			authService.signin(badEmail, password, response);
		});
	}

	@Test
	void signin_비밀번호_불일치() {
		String badPassword = "badPassword";
		HttpServletResponse response = new MockHttpServletResponse();
		given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

		assertThrows(InvalidPasswordException.class, () -> {
			authService.signin(email, badPassword, response);
		});
	}
}