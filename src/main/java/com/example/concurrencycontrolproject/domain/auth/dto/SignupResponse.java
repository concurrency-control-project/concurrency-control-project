package com.example.concurrencycontrolproject.domain.auth.dto;

import java.time.format.DateTimeFormatter;

import com.example.concurrencycontrolproject.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {

	private Long id;
	private String email;
	private String nickname;
	private String phoneNumber;
	private String createdAt;

	public static SignupResponse from(User user) {
		return SignupResponse.builder()
			.id(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.phoneNumber(user.getPhoneNumber())
			.createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.build();
	}
}
