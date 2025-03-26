package com.example.concurrencycontrolproject.domain.schedule.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminScheduleResponse {

	private final Long id;
	private final Long concertId;
	private final String concertTitle;
	private final LocalDateTime datetime;
	private final ScheduleStatus status;

	public static AdminScheduleResponse of(Schedule schedule) {
		return new AdminScheduleResponse(
			schedule.getId(),
			schedule.getConcert().getId(),
			schedule.getConcert().getTitle(),
			schedule.getDatetime(),
			schedule.getStatus()
		);
	}
}
