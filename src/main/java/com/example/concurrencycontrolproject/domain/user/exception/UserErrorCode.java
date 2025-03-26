package com.example.concurrencycontrolproject.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

enum UserErrorCode implements ErrorCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User를 찾을 수 없습니다."),
	ALREADY_DEACTIVATED(HttpStatus.BAD_REQUEST, "ALREADY_DEACTIVATED", "이미 탈퇴한 회원 입니다."),
	SAME_PASSWORD_CHANGE(HttpStatus.BAD_REQUEST, "SAME_PASSWORD_CHANGE", "동일한 비밀번호로 변경할 수 없습니다."),
	INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "INVALID_USER_ROLE", "유효하지 않은 Role 입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "유효하지 않은 비밀번호 입니다."),
	EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "EMAIL_NOT_FOUND", "존재하지 않는 이메일 입니다."),
	EMAIL_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "EMAIL_ACCESS_DENIED", "접근할 수 없는 이메일 입니다."),
	ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "ALREADY_EXISTS_EMAIL", "이미 사용 중인 이메일 입니다.");

	private final HttpStatus status;
	private final String code;
	private final String defaultMessage;

	UserErrorCode(HttpStatus status, String code, String defaultMessage) {
		this.status = status;
		this.code = code;
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return this.status;
	}

	@Override
	public String getDefaultMessage() {
		return this.defaultMessage;
	}
}
