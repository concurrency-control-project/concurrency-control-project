package com.example.concurrencycontrolproject.schedule;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.example.concurrencycontrolproject.domain.schedule.dto.request.CreateScheduleRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.request.UpdateScheduleRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.response.AdminScheduleResponse;
import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;
import com.example.concurrencycontrolproject.domain.schedule.repository.ScheduleRepository;
import com.example.concurrencycontrolproject.domain.schedule.service.ScheduleService;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ScheduleServiceTest {

	// 현재 시큐리티 설정이 미적용된 상태에서 테스트 중
	// 이 테스트에서는 authUser를 null로 처리하여 테스트 진행

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private EntityManager entityManager;

	private Concert concert;

	@BeforeEach
	void setUp() {
		// given: 테스트용 콘서트 저장
		concert = concertRepository.save(
			Concert.builder()
				.title("테스트 콘서트")
				.build()
		);
	}

	@Test
	void saveSchedule_정상_생성() {
		// given: 요청 객체 생성
		LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
		CreateScheduleRequest request = new CreateScheduleRequest(concert.getId(), dateTime);

		// when: 일정 저장
		AdminScheduleResponse response = scheduleService.saveSchedule(null, request);

		// then: 결과 검증
		assertThat(response).isNotNull();
		assertThat(response.getConcertId()).isEqualTo(concert.getId());
		assertThat(response.getDatetime()).isEqualTo(dateTime);
		assertThat(response.getStatus()).isEqualTo(ScheduleStatus.ACTIVE);
	}

	@Test
	void getAdminSchedules_조회_성공() {
		// given: 일정 저장
		LocalDateTime dateTime = LocalDateTime.now().withHour(19);
		scheduleRepository.save(Schedule.of(concert, dateTime, ScheduleStatus.ACTIVE));

		// DB 반영 후 캐시 초기화
		entityManager.flush();
		entityManager.clear();

		// when: 관리자 일정 조회
		var responsePage = scheduleService.getAdminSchedules(null, concert.getId(), dateTime.toLocalDate(), 1, 10);

		// then: 1개 일정 반환
		assertThat(responsePage.getContent()).hasSize(1);
	}

	@Test
	void getSchedule_단건조회_성공() {
		// given: 일정 저장
		LocalDateTime dateTime = LocalDateTime.now();
		Schedule schedule = scheduleRepository.save(Schedule.of(concert, dateTime, ScheduleStatus.ACTIVE));

		// DB 반영 후 캐시 초기화
		entityManager.flush();
		entityManager.clear();

		// when: 단일 일정 조회
		AdminScheduleResponse response = scheduleService.getSchedule(null, concert.getId(), schedule.getId());

		// then: 일정 정보 검증
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(schedule.getId());
		assertThat(response.getConcertId()).isEqualTo(concert.getId());
	}

	@Test
	void updateSchedule_성공() {
		// given: 수정할 일정 저장
		Schedule schedule = scheduleRepository.save(Schedule.of(concert, LocalDateTime.now(), ScheduleStatus.ACTIVE));
		LocalDateTime updatedTime = LocalDateTime.now().plusDays(3);
		UpdateScheduleRequest request = new UpdateScheduleRequest(updatedTime);

		// DB 반영 후 캐시 초기화
		entityManager.flush();
		entityManager.clear();

		// when: 일정 수정
		AdminScheduleResponse response = scheduleService.updateSchedule(null, concert.getId(), schedule.getId(), request);

		// then: 수정된 시간 검증
		assertThat(response.getDatetime()).isEqualTo(updatedTime);
	}

	@Test
	void deleteSchedule_정상삭제() {
		// given: 삭제할 일정 저장
		Schedule schedule = scheduleRepository.save(Schedule.of(concert, LocalDateTime.now(), ScheduleStatus.ACTIVE));

		// DB 반영 후 캐시 초기화
		entityManager.flush();
		entityManager.clear();

		// when: 일정 삭제
		scheduleService.deleteSchedule(null, concert.getId(), schedule.getId());

		// then: 삭제된 일정 상태 확인
		Schedule deleted = scheduleRepository.findById(schedule.getId()).orElseThrow();
		assertThat(deleted.getStatus()).isEqualTo(ScheduleStatus.DELETED);
		assertThat(deleted.getDeletedAt()).isNotNull();
	}
}
