package com.example.concurrencycontrolproject.domain.schedule.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;
import com.example.concurrencycontrolproject.domain.schedule.repository.ScheduleRepository;

@ExtendWith(MockitoExtension.class)
class ScheduleReaderTest {

	@Mock
	private ScheduleRepository scheduleRepository;

	@InjectMocks
	private ScheduleReader scheduleReader;

	@Test
	public void 콘서트하나의_스케줄_조회_정상적으로_조회된다() {
		//given
		Long concertId = 1L;
		Concert concert = Concert.builder().build();
		ReflectionTestUtils.setField(concert, "id", concertId);
		LocalDateTime scheduleDateTime = LocalDateTime.now();
		Schedule schedule = Schedule.of(concert, scheduleDateTime, ScheduleStatus.ACTIVE);
		ReflectionTestUtils.setField(schedule, "id", 1L);
		given(scheduleRepository.findByConcertIdAndStatusIn(anyLong(), any(ScheduleStatus.class))).willReturn(
			List.of(schedule));
		//when
		List<Schedule> schedules = scheduleReader.readActiveSchedulesBy(concertId);
		//then
		assertThat(schedules).hasSize(1);
		assertThat(schedules.get(0))
			.extracting(
				"id",
				"concert",
				"dateTime",
				"status"
			).containsExactly(
				1L,
				concert,
				scheduleDateTime,
				ScheduleStatus.ACTIVE
			);

	}
}