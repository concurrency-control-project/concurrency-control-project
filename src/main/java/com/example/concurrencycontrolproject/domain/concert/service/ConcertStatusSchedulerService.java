package com.example.concurrencycontrolproject.domain.concert.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.entity.ConcertStatus;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcertStatusSchedulerService {

	private final ConcertRepository concertRepository;

	@Transactional
	public void updateConcertsStatus(LocalDateTime requestDateTime) {
		//TODO 성능 테스트 해볼 필요가 있음
		List<Concert> concerts = concertRepository.findByStatusNotIn(
			List.of(ConcertStatus.FINISHED, ConcertStatus.DELETED));

		for (Concert concert : concerts) {
			if (requestDateTime.isBefore(concert.getBookingStartDateTime())) {
				concert.updateStatus(ConcertStatus.PLANNED);
				continue;
			}
			if (requestDateTime.isAfter(concert.getBookingStartDateTime()) && requestDateTime.isBefore(
				concert.getBookingEndDateTime())) {
				concert.updateStatus(ConcertStatus.BOOKING_OPEN);
				continue;
			}
			if (requestDateTime.isAfter(concert.getConcertStartDateTime()) && requestDateTime.isBefore(
				concert.getConcertEndDateTime())) {
				concert.updateStatus(ConcertStatus.PERFORMING);
				continue;
			}

			if (requestDateTime.isAfter(concert.getConcertEndDateTime())) {
				concert.updateStatus(ConcertStatus.FINISHED);
			}
		}

	}
}
