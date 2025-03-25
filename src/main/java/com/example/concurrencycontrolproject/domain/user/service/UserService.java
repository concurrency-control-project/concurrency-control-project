package com.example.concurrencycontrolproject.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.concurrencycontrolproject.domain.user.dto.MyPageResponse;
import com.example.concurrencycontrolproject.domain.user.dto.UserResponse;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.exception.InvalidPasswordException;
import com.example.concurrencycontrolproject.domain.user.exception.UserNotFoundException;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public MyPageResponse getMyPage(Long id) {
		User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
		return MyPageResponse.from(user);
	}

	@Transactional
	public UserResponse updateUser(Long id, String nickname, String phoneNumber) {
		User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
		if (!nickname.isBlank()) {
			user.updateNickname(nickname);
		}
		if (!phoneNumber.isBlank()) {
			user.updatePhoneNumber(phoneNumber);
		}

		User updatedUser = userRepository.findById(id).get();
		return UserResponse.from(updatedUser);
	}

	@Transactional
	public void updatePassword(Long id, String oldPassword, String newPassword) {
		User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
		String userPassword = user.getPassword();

		if (userPassword.equals(oldPassword)) {
			throw new InvalidPasswordException();
		}
	}

	public void deleteUser(Long id) {
	}
}
