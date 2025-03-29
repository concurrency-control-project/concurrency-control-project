package com.example.concurrencycontrolproject.domain.concert.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.schedule.dto.response.ScheduleResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertDetailResponse {

	private final long id;
	private final String title;
	private final String location;
	private final String description;
	private final LocalDateTime concertStartDateTime;
	private final LocalDateTime concertEndDateTime;

	private final List<ScheduleResponse> scheduleResponses;

	@Builder
	private ConcertDetailResponse(long id, String title, String location, String description,
		LocalDateTime concertStartDateTime, LocalDateTime concertEndDateTime,
		List<ScheduleResponse> scheduleResponses) {
		this.id = id;
		this.title = title;
		this.location = location;
		this.description = description;
		this.concertStartDateTime = concertStartDateTime;
		this.concertEndDateTime = concertEndDateTime;
		this.scheduleResponses = scheduleResponses;
	}

	public static ConcertDetailResponse of(Concert concert, List<ScheduleResponse> scheduleResponses) {
		return ConcertDetailResponse.builder()
			.id(concert.getId())
			.title(concert.getTitle())
			.location(concert.getLocation())
			.description(concert.getDescription())
			.concertStartDateTime(concert.getConcertStartDateTime())
			.concertEndDateTime(concert.getConcertEndDateTime())
			.scheduleResponses(scheduleResponses)
			.build();
	}
}
