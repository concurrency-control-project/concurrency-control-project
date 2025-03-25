package com.example.concurrencycontrolproject.domain.user.exception;

public class UserNotFoundException extends UserException {
	public UserNotFoundException() {
		super("User를 찾을 수 없습니다.");
	}
}
