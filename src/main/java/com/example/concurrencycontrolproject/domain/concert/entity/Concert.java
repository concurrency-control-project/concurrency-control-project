package com.example.concurrencycontrolproject.domain.concert.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String performer;
	private String description;
	LocalDateTime concertStartDateTime;
	LocalDateTime concertEndDateTime;
	LocalDateTime bookingStartDateTime;
	LocalDateTime bookingEndDateTime;
	private LocalDate startDate;
	private LocalDate endDate;
	private int runningTime;
	private String location;
	@Enumerated(EnumType.STRING)
	private ConcertStatus status;

	@Builder
	private Concert(String title, String performer, String description, LocalDateTime concertStartDateTime,
		LocalDateTime concertEndDateTime, LocalDateTime bookingStartDateTime, LocalDateTime bookingEndDateTime,
		LocalDate startDate, LocalDate endDate, int runningTime, String location) {
		this.title = title;
		this.performer = performer;
		this.description = description;
		this.concertStartDateTime = concertStartDateTime;
		this.concertEndDateTime = concertEndDateTime;
		this.bookingStartDateTime = bookingStartDateTime;
		this.bookingEndDateTime = bookingEndDateTime;
		this.startDate = startDate;
		this.endDate = endDate;
		this.runningTime = runningTime;
		this.location = location;
		this.status = ConcertStatus.PLANNED;
	}
}
