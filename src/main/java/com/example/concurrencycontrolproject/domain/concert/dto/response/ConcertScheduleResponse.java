package com.example.concurrencycontrolproject.domain.concert.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ConcertScheduleResponse {
	private final long concertScheduleId;
	private final LocalDateTime dateTime;

	private ConcertScheduleResponse(long concertScheduleId, LocalDateTime dateTime) {
		this.concertScheduleId = concertScheduleId;
		this.dateTime = dateTime;
	}

	public static ConcertScheduleResponse of(long concertScheduleId, LocalDateTime dateTime) {
		return new ConcertScheduleResponse(concertScheduleId, dateTime);
	}
}
