package com.example.concurrencycontrolproject.domain.schedule.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {
	CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "CONCERT_NOT_FOUND", "해당 공연을 찾을 수 없습니다."),
	SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_NOT_FOUND", "해당 스케줄을 찾을 수 없습니다."),
	CANNOT_CHANGE_DELETED_STATUS(HttpStatus.BAD_REQUEST, "CANNOT_CHANGE_DELETED_STATUS", "DELETED 상태는 상태 수정 API를 통해 변경할 수 없습니다.");

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