package com.example.concurrencycontrolproject.domain.schedule.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserScheduleResponse {

	// 사용자에게는 ACTIVE 상태인 스케줄만 보이므로 Status 포함x
	private final Long id;
	private final Long concertId;
	private final String concertTitle;
	private final LocalDateTime dateTime;

	private UserScheduleResponse(Long id, Long concertId, String concertTitle, LocalDateTime dateTime) {
		this.id = id;
		this.concertId = concertId;
		this.concertTitle = concertTitle;
		this.dateTime = dateTime;
	}

	public static UserScheduleResponse of(Schedule schedule) {
		return new UserScheduleResponse(
			schedule.getId(),
			schedule.getConcert().getId(),
			schedule.getConcert().getTitle(),
			schedule.getDateTime()
		);
	}
}