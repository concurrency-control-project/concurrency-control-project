package com.example.concurrencycontrolproject.domain.schedule.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleResponse {

	// 다른 도메인에서 스케줄 정보가 필요할 때 재사용 가능한 공통 DTO
	// 외부 API 응답이 아닌 내부 시스템 간 공유용이므로 ScheduleStatus enum 그대로 유지
	private final Long id;
	private final Long concertId;
	private final LocalDateTime dateTime;
	private final ScheduleStatus status;

	// 다른 도메인에서도 직접 생성 가능하도록 public 생성자 유지
	public static ScheduleResponse of(Schedule schedule) {
		return new ScheduleResponse(
			schedule.getId(),
			schedule.getConcert().getId(),
			schedule.getDateTime(),
			schedule.getStatus()
		);
	}
}
