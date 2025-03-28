package com.example.concurrencycontrolproject.domain.schedule.service;

import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.example.concurrencycontrolproject.domain.schedule.dto.request.CreateScheduleRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.request.UpdateScheduleRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.request.UpdateScheduleStatusRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.response.AdminScheduleResponse;
import com.example.concurrencycontrolproject.domain.schedule.dto.response.UserScheduleResponse;
import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;
import com.example.concurrencycontrolproject.domain.schedule.exception.DuplicateScheduleException;
import com.example.concurrencycontrolproject.domain.schedule.exception.ScheduleException;
import com.example.concurrencycontrolproject.domain.schedule.repository.ScheduleRepository;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ScheduleServiceTest {

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;

	private Concert concert;
	private AuthUser adminUser;

	@BeforeEach
	void setup() {
		concert = concertRepository.save(
			Concert.builder()
				.title("Spring Festival")
				.performer("Artist")
				.description("Awesome spring concert")
				.concertStartDateTime(LocalDateTime.of(2025, 5, 1, 18, 0))
				.concertEndDateTime(LocalDateTime.of(2025, 5, 31, 22, 0))
				.bookingStartDateTime(LocalDateTime.of(2025, 4, 1, 0, 0))
				.bookingEndDateTime(LocalDateTime.of(2025, 4, 30, 23, 59))
				.location("Seoul")
				.build()
		);

		adminUser = new AuthUser(1L, "admin@example.com", UserRole.ROLE_ADMIN, "Admin");
	}

	@Test
	void 스케줄_생성_성공() {
		CreateScheduleRequest request = new CreateScheduleRequest(concert.getId(), LocalDateTime.of(2025, 5, 15, 19, 0));
		AdminScheduleResponse response = scheduleService.saveSchedule(adminUser, request);
		assertThat(response.getConcertId()).isEqualTo(concert.getId());
		assertThat(response.getStatus()).isEqualTo("ACTIVE");
	}

	@Test
	void 중복된_스케줄_생성_실패() {
		LocalDateTime dateTime = LocalDateTime.of(2025, 5, 20, 19, 0);
		scheduleRepository.save(Schedule.of(concert, dateTime, ScheduleStatus.ACTIVE));
		CreateScheduleRequest request = new CreateScheduleRequest(concert.getId(), dateTime);
		assertThrows(DuplicateScheduleException.class, () -> scheduleService.saveSchedule(adminUser, request));
	}

	@Test
	void 스케줄_상태_삭제로_변경_실패() {
		Schedule schedule = scheduleRepository.save(Schedule.of(concert, LocalDateTime.of(2025, 5, 21, 20, 0), ScheduleStatus.ACTIVE));
		UpdateScheduleStatusRequest request = new UpdateScheduleStatusRequest(ScheduleStatus.DELETED);
		assertThrows(ScheduleException.class, () -> scheduleService.updateScheduleStatus(adminUser, concert.getId(), schedule.getId(), request));
	}

	@Test
	void 스케줄_상태_변경_성공() {
		Schedule schedule = scheduleRepository.save(Schedule.of(concert, LocalDateTime.of(2025, 5, 21, 20, 0), ScheduleStatus.ACTIVE));
		UpdateScheduleStatusRequest request = new UpdateScheduleStatusRequest(ScheduleStatus.CANCELED);
		AdminScheduleResponse response = scheduleService.updateScheduleStatus(adminUser, concert.getId(), schedule.getId(), request);
		assertThat(response.getStatus()).isEqualTo("CANCELED");
	}

	@Test
	void 스케줄_삭제_성공() {
		Schedule schedule = scheduleRepository.save(Schedule.of(concert, LocalDateTime.of(2025, 5, 22, 20, 0), ScheduleStatus.ACTIVE));
		scheduleService.deleteSchedule(adminUser, concert.getId(), schedule.getId());
		Schedule deleted = scheduleRepository.findById(schedule.getId()).orElseThrow();
		assertThat(deleted.getStatus()).isEqualTo(ScheduleStatus.DELETED);
		assertThat(deleted.getDeletedAt()).isNotNull();
	}

	@Test
	void 스케줄_시간_수정_성공() {
		Schedule schedule = scheduleRepository.save(Schedule.of(concert, LocalDateTime.of(2025, 5, 25, 18, 0), ScheduleStatus.ACTIVE));
		LocalDateTime newTime = LocalDateTime.of(2025, 5, 26, 20, 0);
		UpdateScheduleRequest request = new UpdateScheduleRequest(newTime);
		AdminScheduleResponse response = scheduleService.updateSchedule(adminUser, concert.getId(), schedule.getId(), request);
		assertThat(response.getDateTime()).isEqualTo(newTime);
	}

	@Test
	void 스케줄_단건_조회_성공() {
		Schedule schedule = scheduleRepository.save(Schedule.of(concert, LocalDateTime.of(2025, 5, 10, 18, 0), ScheduleStatus.ACTIVE));
		AdminScheduleResponse response = scheduleService.getSchedule(adminUser, concert.getId(), schedule.getId());
		assertThat(response.getId()).isEqualTo(schedule.getId());
	}

	@Test
	void 관리자용_스케줄_조회_성공() {
		LocalDateTime date = LocalDateTime.of(2025, 5, 5, 18, 0);
		scheduleRepository.save(Schedule.of(concert, date, ScheduleStatus.ACTIVE));
		Page<AdminScheduleResponse> result = scheduleService.getAdminSchedules(adminUser, concert.getId(), date.toLocalDate(), 1, 10);
		assertThat(result.getTotalElements()).isGreaterThan(0);
	}

	@Test
	void 사용자용_스케줄_조회_성공() {
		LocalDateTime date = LocalDateTime.of(2025, 5, 7, 18, 0);
		scheduleRepository.save(Schedule.of(concert, date, ScheduleStatus.ACTIVE));
		Page<UserScheduleResponse> result = scheduleService.getUserSchedules(adminUser, concert.getId(), date.toLocalDate(), 1, 10);
		assertThat(result.getTotalElements()).isGreaterThan(0);
	}
}