package com.example.concurrencycontrolproject.domain.user.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.concurrencycontrolproject.domain.user.dto.MyPageResponse;
import com.example.concurrencycontrolproject.domain.user.dto.UserResponse;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.exception.InvalidPasswordException;
import com.example.concurrencycontrolproject.domain.user.exception.SamePasswordChangeException;
import com.example.concurrencycontrolproject.domain.user.exception.UserAlreadyDeactivatedException;
import com.example.concurrencycontrolproject.domain.user.exception.UserNotFoundException;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public MyPageResponse getMyPage(Long id) {
		User user = invalidCheckUser(id);
		return MyPageResponse.from(user);
	}

	@Transactional
	public UserResponse updateUser(Long id, String nickname, String phoneNumber) {
		User user = invalidCheckUser(id);

		if (!StringUtils.isEmpty(nickname)) {
			user.updateNickname(nickname);
		}
		if (!StringUtils.isEmpty(phoneNumber)) {
			user.updatePhoneNumber(phoneNumber);
		}

		User updatedUser = userRepository.findById(id).get();
		return UserResponse.from(updatedUser);
	}

	@Transactional
	public void updatePassword(Long id, String oldPassword, String newPassword) {
		User user = invalidCheckUser(id);
		String userPassword = user.getPassword();

		if (!passwordEncoder.matches(oldPassword, userPassword)) {
			throw new InvalidPasswordException();
		}

		if (passwordEncoder.matches(newPassword, userPassword)) {
			throw new SamePasswordChangeException();
		}

		user.updatePassword(passwordEncoder.encode(newPassword));
	}

	@Transactional
	public void deleteUser(Long id) {
		invalidCheckUser(id).cancelUser();
	}

	private User invalidCheckUser(Long id) {
		User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
		Optional.ofNullable(user.getDeletedAt())
			.filter(Objects::nonNull)
			.ifPresent(deletedAt -> {
				throw new UserAlreadyDeactivatedException();
			});

		return user;
	}
}
