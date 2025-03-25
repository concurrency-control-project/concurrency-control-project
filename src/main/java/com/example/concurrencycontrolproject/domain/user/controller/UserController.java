package com.example.concurrencycontrolproject.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.common.dto.AuthUser;
import com.example.concurrencycontrolproject.domain.user.dto.MyPageResponse;
import com.example.concurrencycontrolproject.domain.user.dto.UpdatePasswordRequest;
import com.example.concurrencycontrolproject.domain.user.dto.UpdateUserRequest;
import com.example.concurrencycontrolproject.domain.user.dto.UserResponse;
import com.example.concurrencycontrolproject.domain.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/v1/users/my")
	public ResponseEntity<MyPageResponse> getMyPage(@AuthenticationPrincipal AuthUser authUser) {
		return ResponseEntity.ok(userService.getMyPage(authUser.getId()));
	}

	@PatchMapping("/v1/users")
	public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal AuthUser authUser,
		@RequestBody UpdateUserRequest updateUserRequest) {
		return ResponseEntity.ok(
			userService.updateUser(authUser.getId(), updateUserRequest.getNickname(),
				updateUserRequest.getPhoneNumber()));
	}

	@PatchMapping("/v1/users/password")
	public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
		userService.updatePassword(authUser.getId(), updatePasswordRequest.getOldPassword(),
			updatePasswordRequest.getNewPassword());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/v1/users")
	public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal AuthUser authUser) {
		userService.deleteUser(authUser.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
