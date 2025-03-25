package com.example.concurrencycontrolproject.domain.user.exception;

public class UserAlreadyDeactivatedException extends UserException {
	public UserAlreadyDeactivatedException() {
		super(UserErrorCode.ALREADY_DEACTIVATED);
	}
}
