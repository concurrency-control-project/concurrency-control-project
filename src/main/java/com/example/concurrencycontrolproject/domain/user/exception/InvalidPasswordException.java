package com.example.concurrencycontrolproject.domain.user.exception;

public class InvalidPasswordException extends UserException {
	public InvalidPasswordException() {
		super("잘못된 비밀번호 입니다.");
	}
}
