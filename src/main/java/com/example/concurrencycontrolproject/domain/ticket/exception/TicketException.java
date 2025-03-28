package com.example.concurrencycontrolproject.domain.ticket.exception;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class TicketException extends RuntimeException {

	private final ErrorCode errorCode;

	public TicketException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.errorCode = errorCode;
	}

}
