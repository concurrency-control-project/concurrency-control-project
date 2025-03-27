package com.example.concurrencycontrolproject.domain.ticket.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TicketErrorCode implements ErrorCode {
	TICKET_NOT_FOUND(HttpStatus.NOT_FOUND,
		"TICKET_NOT_FOUND", "티켓을 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND,
		"USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
	SCHEDULE_SEAT_BAD_REQUEST(HttpStatus.BAD_REQUEST,
		"SCHEDULE_SEAT_BAD_REQUEST", "올바르지 않거나 선택할 수 없는 좌석입니다."),
	TICKET_UPDATE_INVALID_STATUS(HttpStatus.BAD_REQUEST,
		"TICKET_UPDATE_INVALID_STATUS", "예약된 상태의 티켓만 좌석을 변경할 수 있습니다."),
	TICKET_ACCESS_DENIED(HttpStatus.FORBIDDEN,
		"TICKET_ACCESS_DENIED", "구매하신 티켓이 아닙니다."),
	TICKET_OWNER_MAPPING_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR,
		"TICKET_OWNER_MAPPING_NOT_FOUND", "구매하신 티켓을 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String defaultMessage;

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
