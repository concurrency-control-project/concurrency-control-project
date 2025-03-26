package com.example.concurrencycontrolproject.domain.user.exception;

public class InvalidUserRoleException extends UserException {
	public InvalidUserRoleException() {
		super(UserErrorCode.INVALID_USER_ROLE);
	}
}
