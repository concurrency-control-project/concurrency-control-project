package com.example.concurrencycontrolproject.domain.schedule.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleRequest {

	@NotNull(message = "날짜 선택은 필수입니다.")
	private LocalDateTime datetime;
}
