package com.example.concurrencycontrolproject.domain.schedule.exception;

import org.springframework.http.HttpStatus;

import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {
	CONCERT_NOT_FOUND("CONCERT_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 공연을 찾을 수 없습니다."),
	SCHEDULE_NOT_FOUND("SCHEDULE_NOT_FOUND", HttpStatus.NOT_FOUND, "해당 스케줄을 찾을 수 없습니다."),
	CANNOT_CHANGE_DELETED_STATUS("CANNOT_CHANGE_DELETED_STATUS", HttpStatus.BAD_REQUEST,
		"DELETED 상태는 상태 수정 API를 통해 변경할 수 없습니다."),
	DUPLICATE_SCHEDULE("DUPLICATE_SCHEDULE", HttpStatus.BAD_REQUEST, "같은 시간대의 공연 스케줄이 이미 존재합니다.");

	// status → httpStatus: @Getter가 getHttpStatus()를 자동 생성하도록 필드명 변경
	private final String code;
	private final HttpStatus httpStatus;
	private final String defaultMessage;
}