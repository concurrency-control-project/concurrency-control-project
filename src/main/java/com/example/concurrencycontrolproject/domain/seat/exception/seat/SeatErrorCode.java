package com.example.concurrencycontrolproject.domain.seat.exception.seat;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SeatErrorCode implements ErrorCode {
	INVALID_SEAT_DATA(HttpStatus.BAD_REQUEST, "INVALID_SEAT_DATA", "유효하지 않은 좌석 정보입니다."),
	SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "SEAT_NOT_FOUND", "좌석을 찾을 수 없습니다."),
	SEAT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SEAT_CREATION_FAILED", "좌석 생성에 실패했습니다.");

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

