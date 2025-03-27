package com.example.concurrencycontrolproject.domain.concert.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.concurrencycontrolproject.domain.concert.dto.request.ConcertRequest;
import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertDetailResponse;
import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertResponse;
import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertScheduleResponse;
import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertSearchResponse;
import com.example.concurrencycontrolproject.domain.concert.dto.response.SeatPriceResponse;
import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.exception.CannotDeleteConcertException;
import com.example.concurrencycontrolproject.domain.concert.exception.ConcertAlreadyExistsException;
import com.example.concurrencycontrolproject.domain.concert.exception.InvalidBookingPeriodException;
import com.example.concurrencycontrolproject.domain.concert.exception.InvalidConcertPeriodException;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcertService {

	private final ConcertRepository concertRepository;

	private final ConcertScheduleService concertScheduleService;

	private final ConcertSeatService concertSeatService;

	private final ConcertReader concertReader;

	public ConcertResponse createConcert(ConcertRequest request) {

		validateConcertPeriod(request.getConcertEndDateTime(), request.getConcertStartDateTime());

		validateBookingPeriod(request.getBookingEndDateTime(), request.getBookingStartDateTime());

		validateBookingEndBeforeConcertStart(request.getBookingEndDateTime(), request.getConcertStartDateTime());

		validateDuplicateConcert(request.getConcertStartDateTime(), request.getLocation());

		Concert concert = request.toEntity();

		Concert savedConcert = concertRepository.save(concert);

		return ConcertResponse.from(savedConcert);
	}

	public Page<ConcertSearchResponse> searchConcerts(String keyword, Pageable pageable) {
		Page<Concert> concerts = concertRepository.search(keyword, pageable);
		return concerts.map(ConcertSearchResponse::from);
	}

	public ConcertDetailResponse getConcertWithScheduleAndSeats(Long concertId) {

		//TODO
		Concert concert = concertReader.readNotDeleted(concertId);
		List<ConcertScheduleResponse> concertScheduleResponses = concertScheduleService.getConcertScheduleResponses(
			concert.getId());
		List<SeatPriceResponse> seatPriceResponses = concertSeatService.getSeatPriceResponses(concertId);
		return ConcertDetailResponse.of(concert, concertScheduleResponses, seatPriceResponses);
	}

	@Transactional
	public void updateConcert(Long concertId, ConcertRequest request) {

		Concert concert = concertReader.read(concertId);

		if (isConcertStartDateTimeChanged(request.getConcertStartDateTime(), concert.getConcertStartDateTime())
			|| isConcertEndDateTimeChanged(request.getConcertEndDateTime(), concert.getConcertEndDateTime())) {
			validateConcertPeriod(request.getConcertEndDateTime(), request.getConcertStartDateTime());
		}

		if (isBookingStartDateTimeChanged(request.getBookingStartDateTime(), concert.getBookingStartDateTime())
			|| isBookingEndDateTimeChanged(request.getBookingEndDateTime(), concert.getBookingEndDateTime())) {
			validateBookingPeriod(request.getBookingEndDateTime(), request.getBookingStartDateTime());
		}

		if (isConcertStartDateTimeChanged(request.getConcertStartDateTime(), concert.getConcertStartDateTime())
			|| isBookingEndDateTimeChanged(request.getBookingEndDateTime(), concert.getBookingEndDateTime())) {
			validateBookingEndBeforeConcertStart(request.getBookingEndDateTime(), request.getConcertStartDateTime());
		}

		if (isLocationChanged(request.getLocation(), concert.getLocation()) || isConcertStartDateTimeChanged(
			request.getConcertStartDateTime(), concert.getConcertStartDateTime())) {
			if (concertRepository.existsByConcertStartDateTimeAndLocationAndIdNot(request.getConcertStartDateTime(),
				request.getLocation(), concertId)) {
				throw new ConcertAlreadyExistsException();
			}
		}

		concert.update(request.getTitle(), request.getPerformer(), request.getDescription(),
			request.getConcertStartDateTime(), request.getConcertEndDateTime(), request.getBookingStartDateTime(),
			request.getBookingEndDateTime(), request.getLocation());

	}

	@Transactional
	public void deleteConcert(Long concertId) {
		Concert concert = concertReader.readNotDeleted(concertId);

		if (!concert.isPlanned()) {
			throw new CannotDeleteConcertException();
		}
		concert.softDelete();
	}

	private boolean isLocationChanged(String location, String currentLocation) {
		return StringUtils.hasText(location) && !Objects.equals(location, currentLocation);
	}

	private boolean isBookingEndDateTimeChanged(LocalDateTime bookingEndDateTime,
		LocalDateTime currentBookingEndDateTime) {
		return bookingEndDateTime != null && !Objects.equals(bookingEndDateTime, currentBookingEndDateTime);
	}

	private boolean isBookingStartDateTimeChanged(LocalDateTime bookingStartDateTime,
		LocalDateTime currentBookingStartDateTime) {
		return bookingStartDateTime != null && !Objects.equals(bookingStartDateTime, currentBookingStartDateTime);
	}

	private boolean isConcertEndDateTimeChanged(LocalDateTime concertEndDateTime,
		LocalDateTime currentConcertEndDateTime) {
		return concertEndDateTime != null && !Objects.equals(concertEndDateTime, currentConcertEndDateTime);
	}

	private boolean isConcertStartDateTimeChanged(LocalDateTime concertStartDateTime,
		LocalDateTime currentConcertStartDateTime) {
		return concertStartDateTime != null && !Objects.equals(concertStartDateTime, currentConcertStartDateTime);
	}

	private static void validateConcertPeriod(LocalDateTime concertEndDateTime, LocalDateTime concertStartDateTime) {
		if (concertEndDateTime.isBefore(concertStartDateTime)) {
			throw new InvalidConcertPeriodException();
		}
	}

	private static void validateBookingPeriod(LocalDateTime bookingEndDateTime, LocalDateTime bookingStartDateTime) {
		if (bookingEndDateTime.isBefore(bookingStartDateTime)) {
			throw new InvalidBookingPeriodException();
		}

	}

	private void validateDuplicateConcert(LocalDateTime concertStartDateTime, String location) {
		if (concertRepository.existsByConcertStartDateTimeAndLocation(concertStartDateTime, location)) {
			throw new ConcertAlreadyExistsException();
		}
	}

	private static void validateBookingEndBeforeConcertStart(LocalDateTime bookingEndDateTime,
		LocalDateTime concertStartDateTime) {
		if (bookingEndDateTime.isAfter(concertStartDateTime)) {
			throw new InvalidBookingPeriodException("콘서트 예약 종료일은 콘서트 시작일보다 이전이여야 합니다.");
		}
	}

}
