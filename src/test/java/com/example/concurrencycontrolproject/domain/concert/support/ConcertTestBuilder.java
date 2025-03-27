package com.example.concurrencycontrolproject.domain.concert.support;

import java.time.LocalDateTime;

import org.springframework.test.util.ReflectionTestUtils;

import com.example.concurrencycontrolproject.domain.concert.dto.request.ConcertRequest;
import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.entity.ConcertStatus;

public class ConcertTestBuilder {

	private long id;
	private String title;
	private String performer;
	private String description;
	private LocalDateTime concertStartDateTime;
	private LocalDateTime concertEndDateTime;
	private LocalDateTime bookingStartDateTime;
	private LocalDateTime bookingEndDateTime;
	private String location;
	private ConcertStatus status;

	public ConcertTestBuilder withId(Long id) {
		this.id = id;
		return this;
	}

	public ConcertTestBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	public ConcertTestBuilder withPerformer(String performer) {
		this.performer = performer;
		return this;
	}

	public ConcertTestBuilder withDescription(String description) {
		this.description = description;
		return this;
	}

	public ConcertTestBuilder withConcertStartDateTime(LocalDateTime concertStartDateTime) {
		this.concertStartDateTime = concertStartDateTime;
		return this;
	}

	public ConcertTestBuilder withConcertEndDateTime(LocalDateTime concertEndDateTime) {
		this.concertEndDateTime = concertEndDateTime;
		return this;
	}

	public ConcertTestBuilder withBookingStartDateTime(LocalDateTime bookingStartDateTime) {
		this.bookingStartDateTime = bookingStartDateTime;
		return this;
	}

	public ConcertTestBuilder withBookingEndDateTime(LocalDateTime bookingEndDateTime) {
		this.bookingEndDateTime = bookingEndDateTime;
		return this;
	}

	public ConcertTestBuilder withLocation(String location) {
		this.location = location;
		return this;
	}

	public ConcertTestBuilder withStatus(ConcertStatus status) {
		this.status = status;
		return this;
	}

	public ConcertRequest buildConcertRequest() {
		return ConcertRequest.builder()
			.title(title)
			.performer(performer)
			.description(description)
			.concertStartDateTime(concertStartDateTime)
			.concertEndDateTime(concertEndDateTime)
			.bookingStartDateTime(bookingStartDateTime)
			.bookingEndDateTime(bookingEndDateTime)
			.location(location)
			.build();
	}

	public Concert buildConcert() {
		Concert concert = Concert.builder()
			.title(title)
			.performer(performer)
			.description(description)
			.concertStartDateTime(concertStartDateTime)
			.concertEndDateTime(concertEndDateTime)
			.bookingStartDateTime(bookingStartDateTime)
			.bookingEndDateTime(bookingEndDateTime)
			.location(location)
			.build();
		ReflectionTestUtils.setField(concert, "status", status);
		ReflectionTestUtils.setField(concert, "id", id);
		return concert;
	}

}
