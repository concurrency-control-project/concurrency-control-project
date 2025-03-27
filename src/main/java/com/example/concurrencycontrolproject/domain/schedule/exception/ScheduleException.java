package com.example.concurrencycontrolproject.domain.schedule.exception;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ScheduleException extends RuntimeException {

	private final ErrorCode errorCode;

	public ScheduleException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.errorCode = errorCode;
	}
}
