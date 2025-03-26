package com.example.concurrencycontrolproject.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

public class UserException extends RuntimeException {
	private HttpStatus status;

	UserException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.status = errorCode.getHttpStatus();
	}
}
