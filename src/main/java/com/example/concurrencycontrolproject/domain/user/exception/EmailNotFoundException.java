package com.example.concurrencycontrolproject.domain.user.exception;

public class EmailNotFoundException extends UserException {
	public EmailNotFoundException() {
		super("존재하지 않는 이메일 입니다.");
	}
}
