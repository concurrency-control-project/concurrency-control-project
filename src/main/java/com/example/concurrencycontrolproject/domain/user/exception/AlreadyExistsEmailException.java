package com.example.concurrencycontrolproject.domain.user.exception;

public class AlreadyExistsEmailException extends UserException {
	public AlreadyExistsEmailException() {
		super("이미 사용 중인 이메일 입니다.");
	}
}
