package com.example.concurrencycontrolproject.domain.user.dto;

import java.time.format.DateTimeFormatter;

import com.example.concurrencycontrolproject.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

	private String email;
	private String nickname;
	private String role;
	private String phoneNumber;
	private String modifiedAt;

	public static UserResponse from(User user) {
		return UserResponse.builder()
			.email(user.getEmail())
			.nickname(user.getNickname())
			.role(user.getRole().name())
			.phoneNumber(user.getPhoneNumber())
			.modifiedAt(user.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.build();
	}
}
