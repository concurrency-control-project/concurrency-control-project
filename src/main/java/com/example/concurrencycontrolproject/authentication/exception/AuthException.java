package com.example.concurrencycontrolproject.authentication.exception;

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
