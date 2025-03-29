package com.example.concurrencycontrolproject.domain.schedule.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;
import com.example.concurrencycontrolproject.domain.schedule.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleReader {

	private final ScheduleRepository scheduleRepository;

	public List<Schedule> readActiveSchedulesBy(Long concertId) {
		return scheduleRepository.findByConcertIdAndStatusIn(concertId, ScheduleStatus.ACTIVE);
	}
}
