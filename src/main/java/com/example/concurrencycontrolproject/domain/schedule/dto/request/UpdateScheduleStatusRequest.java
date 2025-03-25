package com.example.concurrencycontrolproject.domain.schedule.dto.request;

import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleStatusRequest {

	@NotNull(message = "상태 값은 필수입니다.")
	private ScheduleStatus status;
}
