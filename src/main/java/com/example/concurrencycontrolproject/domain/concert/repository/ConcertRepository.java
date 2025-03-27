package com.example.concurrencycontrolproject.domain.concert.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.entity.ConcertStatus;

public interface ConcertRepository extends JpaRepository<Concert, Long>, ConcertSearchRepository {

	boolean existsByConcertStartDateTimeAndLocation(LocalDateTime concertStartDateTime, String location);

	boolean existsByConcertStartDateTimeAndLocationAndIdNot(LocalDateTime concertStartDateTime, String location,
		Long concertId);

	Optional<Concert> findByIdAndStatusNot(Long concertId, ConcertStatus concertStatus);
}
