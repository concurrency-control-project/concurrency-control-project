package com.example.concurrencycontrolproject.domain.user.dto;

import java.time.format.DateTimeFormatter;

import com.example.concurrencycontrolproject.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageResponse {

	private Long id;
	private String email;
	private String nickname;
	private String role;
	private String phoneNumber;
	private String createdAt;
	private String modifiedAt;

	public static MyPageResponse from(User user) {
		return MyPageResponse.builder()
			.id(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.role(user.getRole().name())
			.phoneNumber(user.getPhoneNumber())
			.createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.modifiedAt(user.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.build();
	}
}
