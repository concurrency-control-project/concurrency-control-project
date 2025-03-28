package com.example.concurrencycontrolproject.domain.schedule.exception;

public class ConcertNotFoundException extends ScheduleException {
	public ConcertNotFoundException() {
		super(ScheduleErrorCode.CONCERT_NOT_FOUND);
	}
}
