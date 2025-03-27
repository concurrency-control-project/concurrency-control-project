package com.example.concurrencycontrolproject.domain.concert.service;

import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.entity.ConcertStatus;
import com.example.concurrencycontrolproject.domain.concert.exception.ConcertNotFoundException;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcertReader {

	private final ConcertRepository concertRepository;

	public Concert read(Long concertId) {
		return concertRepository.findById(concertId)
			.orElseThrow(() -> new ConcertNotFoundException("콘서트를 찾을 수 없습니다"));
	}

	public Concert readNotDeleted(Long concertId) {
		return concertRepository.findByIdAndStatusNot(concertId, ConcertStatus.DELETED)
			.orElseThrow(() -> new ConcertNotFoundException("콘서트를 찾을 수 없습니다"));
	}

}
