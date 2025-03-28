package com.example.concurrencycontrolproject.authentication.auth.dto;

import java.time.format.DateTimeFormatter;

import com.example.concurrencycontrolproject.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {

	private Long id;
	private String social;
	private String email;
	private String nickname;
	private String phoneNumber;
	private String createdAt;

	public static UserProfileResponse from(User user) {
		return UserProfileResponse.builder()
			.id(user.getId())
			.social(user.getSocial().name())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.phoneNumber(user.getPhoneNumber())
			.createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.build();
	}
}
