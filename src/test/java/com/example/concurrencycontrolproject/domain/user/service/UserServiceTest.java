package com.example.concurrencycontrolproject.domain.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.concurrencycontrolproject.domain.user.dto.MyPageResponse;
import com.example.concurrencycontrolproject.domain.user.dto.UserResponse;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;
import com.example.concurrencycontrolproject.domain.user.exception.InvalidPasswordException;
import com.example.concurrencycontrolproject.domain.user.exception.SamePasswordChangeException;
import com.example.concurrencycontrolproject.domain.user.exception.UserAlreadyDeactivatedException;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Spy
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	User user;
	Long userId = 1L;
	String nickname = "nickname";
	String phoneNumber = "010-0000-0000";
	String password = "password";
	String encodePassword;

	@BeforeEach
	void setUp() {
		encodePassword = passwordEncoder.encode(password);

		user = new User("email", encodePassword, nickname, phoneNumber);
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(user, "modifiedAt", LocalDateTime.now());
	}

	@Test
	@Order(0)
	void getMyPage() {
		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		MyPageResponse myPage = userService.getMyPage(userId);

		assertThat(myPage).isNotNull();
		assertThat(myPage.getId()).isEqualTo(userId);
		assertThat(myPage.getRole()).isEqualTo(UserRole.ROLE_USER.name());
	}

	@Test
	@Order(0)
	void updateUser() {
		String newNickname = "new_nickname";
		String newPhoneNumber = "010-9999-9999";

		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		UserResponse userResponse = userService.updateUser(userId, newNickname, newPhoneNumber);

		assertThat(userResponse).isNotNull();
		assertThat(userResponse.getNickname()).isEqualTo(user.getNickname());
		assertThat(userResponse.getNickname()).isNotEqualTo(nickname);
		assertThat(userResponse.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
		assertThat(userResponse.getPhoneNumber()).isNotEqualTo(phoneNumber);
	}

	@Test
	@Order(0)
	void updateUser_phoneNumber가_null이면_변하지_않는다() {
		String newNickname = "new_nickname";
		String newPhoneNumber = null;

		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		UserResponse userResponse = userService.updateUser(userId, newNickname, newPhoneNumber);

		assertThat(userResponse).isNotNull();
		assertThat(userResponse.getPhoneNumber()).isEqualTo(phoneNumber);
		assertThat(userResponse.getPhoneNumber()).isNotEqualTo(newPhoneNumber);
	}

	@Test
	@Order(3)
	void updatePassword() {
		String newPassword = "Abcd12345678!";
		String newEncode = passwordEncoder.encode(newPassword);

		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
		given(passwordEncoder.encode(anyString())).willReturn(newEncode);

		assertThat(user.getPassword()).isEqualTo(encodePassword);

		userService.updatePassword(userId, password, newPassword);

		assertThat(passwordEncoder.matches(newPassword, newEncode)).isTrue();
		assertThat(user.getPassword()).isEqualTo(newEncode);
	}

	@Test
	@Order(2)
	void updatePassword_oldPassword가_일치하지_않으면_오류_발생() {
		String oldPassword = "badPassword";
		String newPassword = "Abcd12345678!";

		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		assertThrows(InvalidPasswordException.class, () -> {
			userService.updatePassword(userId, oldPassword, newPassword);
		});
	}

	@Test
	@Order(1)
	void updatePassword_oldPassword와_newPassowrd가_일치하면_오류_발생() {
		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		assertThrows(SamePasswordChangeException.class, () -> {
			userService.updatePassword(userId, password, password);
		});
	}

	@Test
	@Order(5)
	void deleteUser_이미_탈퇴한_사용자는_예외_발생() {
		ReflectionTestUtils.setField(user, "deletedAt", LocalDateTime.now());

		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		assertThrows(UserAlreadyDeactivatedException.class, () -> {
			userService.deleteUser(userId);
		});
	}

	@Test
	@Order(4)
	void deleteUser() {
		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
		userService.deleteUser(userId);
		assertThat(user.getDeletedAt()).isNotNull();
	}
}