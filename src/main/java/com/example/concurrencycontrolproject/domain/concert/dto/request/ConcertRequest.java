package com.example.concurrencycontrolproject.domain.concert.dto.request;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertRequest {

	@NotEmpty(message = "제목은 필수 값입니다.")
	private String title;
	@NotEmpty(message = "공연자는 필수 값입니다.")
	private String performer;

	private String description;

	@NotNull(message = "시작일은 필수 값입니다.")
	private LocalDateTime concertStartDateTime;

	@NotNull(message = "종료일은 필수 값입니다.")
	private LocalDateTime concertEndDateTime;

	@NotNull(message = "예약 시작일은 필수 값입니다.")
	private LocalDateTime bookingStartDateTime;

	@NotNull(message = "예약 종료일은 필수 값입니다.")
	private LocalDateTime bookingEndDateTime;

	@NotEmpty(message = "장소는 필수 값입니다.")
	private String location;

	public ConcertRequest() {
	}

	@Builder
	private ConcertRequest(String title, String performer, String description, LocalDateTime concertStartDateTime,
		LocalDateTime concertEndDateTime, LocalDateTime bookingStartDateTime, LocalDateTime bookingEndDateTime,
		String location) {
		this.title = title;
		this.performer = performer;
		this.description = description;
		this.concertStartDateTime = concertStartDateTime;
		this.concertEndDateTime = concertEndDateTime;
		this.bookingStartDateTime = bookingStartDateTime;
		this.bookingEndDateTime = bookingEndDateTime;
		this.location = location;
	}

	public Concert toEntity() {
		return Concert.builder()
			.title(this.getTitle())
			.performer(this.getPerformer())
			.description(this.getDescription())
			.concertStartDateTime(this.getConcertStartDateTime())
			.concertEndDateTime(this.getConcertEndDateTime())
			.bookingStartDateTime(this.getBookingStartDateTime())
			.bookingEndDateTime(this.getBookingEndDateTime())
			.location(this.getLocation())
			.build();
	}
}
