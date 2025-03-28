package com.example.concurrencycontrolproject.domain.schedule.exception;

public class DuplicateScheduleException extends ScheduleException {
	public DuplicateScheduleException() {
		super(ScheduleErrorCode.DUPLICATE_SCHEDULE);
	}
}
