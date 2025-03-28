package com.example.concurrencycontrolproject.domain.schedule.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SchedulePageResult {
	private List<AdminScheduleResponse> data;
	private int pageNumber;
	private int pageSize;
	private int totalPages;
	private long totalElements;
}
