package com.example.concurrencycontrolproject.domain.concert.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertStatus {

	PLANNED("예정"),
	BOOKING_OPEN("예매 중"),
	PERFORMING("공연 중"),
	FINISHED("공연 종료"),
	DELETED("삭제된 공연");

	private final String description;

}
