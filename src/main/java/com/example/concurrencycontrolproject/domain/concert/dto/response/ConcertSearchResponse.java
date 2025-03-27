package com.example.concurrencycontrolproject.domain.concert.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertSearchResponse {
	private final long concertId;
	private final String title;
	private final String performer;
	private final String description;
	private final LocalDateTime concertStartDateTime;
	private final LocalDateTime concertEndDateTime;
	private final String location;
	private final String concertStatus;

	@Builder
	private ConcertSearchResponse(long concertId, String title, String performer, String description,
		LocalDateTime concertStartDateTime, LocalDateTime concertEndDateTime, String location, String concertStatus) {
		this.concertId = concertId;
		this.title = title;
		this.performer = performer;
		this.description = description;
		this.concertStartDateTime = concertStartDateTime;
		this.concertEndDateTime = concertEndDateTime;
		this.location = location;
		this.concertStatus = concertStatus;
	}

	public static ConcertSearchResponse from(Concert concert) {
		return ConcertSearchResponse.builder()
			.concertId(concert.getId())
			.title(concert.getTitle())
			.performer(concert.getPerformer())
			.description(concert.getDescription())
			.concertStartDateTime(concert.getConcertStartDateTime())
			.concertEndDateTime(concert.getConcertEndDateTime())
			.location(concert.getLocation())
			.concertStatus(concert.getStatus().getDescription())
			.build();
	}
}
