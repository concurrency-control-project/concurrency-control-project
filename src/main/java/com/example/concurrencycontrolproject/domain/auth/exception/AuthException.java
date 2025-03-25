package com.example.concurrencycontrolproject.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
	private ErrorCode errorCode;
	private HttpStatus status;

	AuthException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.errorCode = errorCode;
		this.status = errorCode.getHttpStatus();
	}
}
