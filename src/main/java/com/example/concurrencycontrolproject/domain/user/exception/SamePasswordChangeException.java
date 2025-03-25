package com.example.concurrencycontrolproject.domain.user.exception;

public class SamePasswordChangeException extends UserException {
	public SamePasswordChangeException() {
		super("동일한 비밀번호로 변경할 수 없습니다.");
	}
}
