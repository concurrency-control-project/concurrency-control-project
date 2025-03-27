package com.example.concurrencycontrolproject.domain.concert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.concert.dto.response.SeatPriceResponse;

@Service
public class ConcertSeatService {
	public List<SeatPriceResponse> getSeatPriceResponses(Long concertId) {
		return List.of();
	}
}
