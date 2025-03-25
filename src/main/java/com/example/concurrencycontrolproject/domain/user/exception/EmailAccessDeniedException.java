package com.example.concurrencycontrolproject.domain.user.exception;

public class EmailAccessDeniedException extends UserException {
	public EmailAccessDeniedException() {
		super("접근할 수 없는 이메일 입니다.");
	}
}
