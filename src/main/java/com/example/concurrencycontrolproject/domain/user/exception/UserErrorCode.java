package com.example.concurrencycontrolproject.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

enum UserErrorCode implements ErrorCode {
	INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "INVALID_USER_ROLE", "유효하지 않은 Role 입니다.");

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
