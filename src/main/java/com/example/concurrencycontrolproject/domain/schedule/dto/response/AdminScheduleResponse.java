package com.example.concurrencycontrolproject.domain.schedule.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class AdminScheduleResponse {

	private final Long id;
	private final Long concertId;
	private final String concertTitle;
	private final LocalDateTime dateTime;
	private final String status;

	private AdminScheduleResponse(Long id, Long concertId, String concertTitle, LocalDateTime dateTime, String status) {
		this.id = id;
		this.concertId = concertId;
		this.concertTitle = concertTitle;
		this.dateTime = dateTime;
		this.status = status;
	}

	public static AdminScheduleResponse of(Schedule schedule) {
		return new AdminScheduleResponse(
			schedule.getId(),
			schedule.getConcert().getId(),
			schedule.getConcert().getTitle(),
			schedule.getDateTime(),
			schedule.getStatus().name() // Enum -> String
		);
	}
}
