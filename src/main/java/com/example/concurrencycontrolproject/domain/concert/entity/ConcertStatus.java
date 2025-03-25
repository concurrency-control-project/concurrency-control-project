package com.example.concurrencycontrolproject.domain.concert.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertStatus {

	PLANNED("예정"),
	BOOKING_OPEN("예매 중"),
	CLOSED("판매 종료"),
	CANCELLED("공연 취소"),
	FINISHED("공연 종료"),
	DELETED("삭제된 공연");

	private final String description;

}
