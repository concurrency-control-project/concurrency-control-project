package com.example.concurrencycontrolproject.domain.schedule.exception;

public class ScheduleOutOfConcertRangeException extends ScheduleException {
	public ScheduleOutOfConcertRangeException() {
		super(ScheduleErrorCode.SCHEDULE_OUT_OF_CONCERT_RANGE);
	}
}
