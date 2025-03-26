package com.example.concurrencycontrolproject.domain.seat.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduledSeatErrorCode implements ErrorCode {
	SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "SEAT_ALREADY_RESRVED", "이미 예약된 자석입니다."),
	SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "SEAT_NOT_FOUND", "해당 좌석을 찾을 수 없습니다."),
	INVALID_SCHEDULE(HttpStatus.BAD_REQUEST, "INVALID_SCHEDULE", "유효하지 않은 스케줄입니다.");

	private final HttpStatus status;
	private final String code;
	private final String defaultMessage;

	// ScheduledSeatErrorCode(HttpStatus status, String code, String defaultMessage) {
	// 	this.status = status;
	// 	this.code = code;
	// 	this.defaultMessage = defaultMessage;
	// }

	// @Override
	// public String getCode() {
	// 	return this.code;
	// }
	//
	// @Override
	// public HttpStatus getHttpStatus() {
	// 	return this.status;
	// }
	//
	// @Override
	// public String getDefaultMessage() {
	// 	return this.defaultMessage;
	// }
}
