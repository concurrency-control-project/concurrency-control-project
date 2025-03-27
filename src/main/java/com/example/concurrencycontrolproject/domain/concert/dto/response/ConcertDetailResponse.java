package com.example.concurrencycontrolproject.domain.concert.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;

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

	private final List<ConcertScheduleResponse> concertScheduleResponses;
	private final List<SeatPriceResponse> seatPriceResponses;

	@Builder
	private ConcertDetailResponse(long id, String title, String location, String description,
		LocalDateTime concertStartDateTime, LocalDateTime concertEndDateTime,
		List<ConcertScheduleResponse> concertScheduleResponses, List<SeatPriceResponse> seatPriceResponses) {
		this.id = id;
		this.title = title;
		this.location = location;
		this.description = description;
		this.concertStartDateTime = concertStartDateTime;
		this.concertEndDateTime = concertEndDateTime;
		this.concertScheduleResponses = concertScheduleResponses;
		this.seatPriceResponses = seatPriceResponses;
	}

	public static ConcertDetailResponse of(Concert concert, List<ConcertScheduleResponse> concertScheduleResponses,
		List<SeatPriceResponse> seatPriceResponses) {
		return ConcertDetailResponse.builder()
			.id(concert.getId())
			.title(concert.getTitle())
			.location(concert.getLocation())
			.description(concert.getDescription())
			.concertStartDateTime(concert.getConcertStartDateTime())
			.concertEndDateTime(concert.getConcertEndDateTime())
			.concertScheduleResponses(concertScheduleResponses)
			.seatPriceResponses(seatPriceResponses)
			.build();
	}
}
