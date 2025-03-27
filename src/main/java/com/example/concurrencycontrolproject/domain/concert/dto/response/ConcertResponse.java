package com.example.concurrencycontrolproject.domain.concert.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.entity.ConcertStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertResponse {

	private final Long id;
	private final String title;
	private final String performer;
	private final String description;
	private final LocalDateTime concertStartDateTime;
	private final LocalDateTime concertEndDateTime;
	private final LocalDateTime bookingStartDateTime;
	private final LocalDateTime bookingEndDateTime;
	private final String location;
	private final ConcertStatus status;

	@Builder
	private ConcertResponse(Long id, String title, String performer, String description,
		LocalDateTime concertStartDateTime, LocalDateTime concertEndDateTime, LocalDateTime bookingStartDateTime,
		LocalDateTime bookingEndDateTime, String location, ConcertStatus status) {
		this.id = id;
		this.title = title;
		this.performer = performer;
		this.description = description;
		this.concertStartDateTime = concertStartDateTime;
		this.concertEndDateTime = concertEndDateTime;
		this.bookingStartDateTime = bookingStartDateTime;
		this.bookingEndDateTime = bookingEndDateTime;
		this.location = location;
		this.status = status;
	}

	public static ConcertResponse from(Concert concert) {
		return ConcertResponse.builder()
			.id(concert.getId())
			.title(concert.getTitle())
			.performer(concert.getPerformer())
			.description(concert.getDescription())
			.concertStartDateTime(concert.getConcertStartDateTime())
			.concertEndDateTime(concert.getConcertEndDateTime())
			.bookingStartDateTime(concert.getBookingStartDateTime())
			.bookingEndDateTime(concert.getBookingEndDateTime())
			.location(concert.getLocation())
			.status(concert.getStatus())
			.build();

	}
}
