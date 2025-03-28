package com.example.concurrencycontrolproject.domain.schedule.exception;

public class CannotChangeDeletedStatusException extends ScheduleException {
	public CannotChangeDeletedStatusException() {
		super(ScheduleErrorCode.CANNOT_CHANGE_DELETED_STATUS);
	}
}