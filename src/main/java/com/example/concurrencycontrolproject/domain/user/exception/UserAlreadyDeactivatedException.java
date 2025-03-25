package com.example.concurrencycontrolproject.domain.user.exception;

public class UserAlreadyDeactivatedException extends UserException {
	public UserAlreadyDeactivatedException() {
		super("이미 탈퇴한 회원 입니다.");
	}
}
