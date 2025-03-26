package com.example.concurrencycontrolproject.domain.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidResponse {
	private String filedName;
	private String message;

	public static ValidResponse of(String filedName, String message) {
		return ValidResponse.builder().filedName(filedName).message(message).build();
	}
}
