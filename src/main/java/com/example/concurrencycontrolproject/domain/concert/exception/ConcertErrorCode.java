package com.example.concurrencycontrolproject.domain.concert.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public enum ConcertErrorCode implements ErrorCode {

	CONCERT_NOT_FOUND("CONCERT_001", HttpStatus.NOT_FOUND, "콘서트를 찾을 수 없습니다."),
	CONCERT_ALREADY_EXISTS("CONCERT_002", HttpStatus.CONFLICT, "이미 같은 시간과 장소에 등록된 콘서트가 있습니다."),
	CANNOT_DELETE_CONCERT("CONCERT_003", HttpStatus.BAD_REQUEST, "예매 전 상태의 공연만 삭제할 수 있습니다."),
	CANNOT_MODIFY_CONCERT("CONCERT_004", HttpStatus.BAD_REQUEST, "해당 콘서트는 수정할 수 없습니다."),
	INVALID_CONCERT_PERIOD("CONCERT_005", HttpStatus.BAD_REQUEST, "콘서트 종료일은 콘서트 시작일보다 이후여야 합니다."),
	INVALID_BOOKING_PERIOD("CONCERT_006", HttpStatus.BAD_REQUEST, "콘서트 예약종료일은 예약시작일보다 이후여야 합니다.");

	private final String code;
	private final HttpStatus httpStatus;
	private final String defaultMessage;

	ConcertErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.defaultMessage = defaultMessage;
	}
}
