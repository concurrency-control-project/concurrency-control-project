package com.example.concurrencycontrolproject.domain.schedule.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleResponse {

	private final Long id;
	private final Long concertId;
	private final LocalDateTime datetime;
	private final ScheduleStatus status;

	public static ScheduleResponse of(Schedule schedule) {
		return new ScheduleResponse(
			schedule.getId(),
			schedule.getConcert().getId(),
			schedule.getDatetime(),
			schedule.getStatus()
		);
	}
}
