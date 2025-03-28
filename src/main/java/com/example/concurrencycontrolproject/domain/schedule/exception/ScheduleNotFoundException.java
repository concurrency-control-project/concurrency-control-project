package com.example.concurrencycontrolproject.domain.schedule.exception;

public class ScheduleNotFoundException extends ScheduleException {
	public ScheduleNotFoundException() {
		super(ScheduleErrorCode.SCHEDULE_NOT_FOUND);
	}
}
