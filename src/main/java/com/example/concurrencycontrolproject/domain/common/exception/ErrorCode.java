package com.example.concurrencycontrolproject.domain.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	String getCode();

	HttpStatus getHttpStatus();

	String getDefaultMessage();
}
