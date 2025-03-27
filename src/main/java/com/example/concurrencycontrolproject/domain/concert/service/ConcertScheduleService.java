package com.example.concurrencycontrolproject.domain.concert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertScheduleResponse;

@Service
public class ConcertScheduleService {
	public List<ConcertScheduleResponse> getConcertScheduleResponses(Long id) {
		return List.of();
	}
}
