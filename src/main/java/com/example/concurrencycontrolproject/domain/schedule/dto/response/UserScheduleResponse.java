package com.example.concurrencycontrolproject.domain.schedule.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserScheduleResponse {

	private final Long id;
	private final Long concertId;
	private final String concertTitle;
	private final LocalDateTime datetime;
	// 사용자에게는 ACTIVE 상태인 스케줄만 보이므로 Status 포함x

	public static UserScheduleResponse of(Schedule schedule) {
		return new UserScheduleResponse(
			schedule.getId(),
			schedule.getConcert().getId(),
			schedule.getConcert().getTitle(),
			schedule.getDatetime()
		);
	}
}