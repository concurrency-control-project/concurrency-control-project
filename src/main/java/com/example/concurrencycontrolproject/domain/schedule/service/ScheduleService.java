package com.example.concurrencycontrolproject.domain.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.example.concurrencycontrolproject.domain.schedule.exception.ScheduleErrorCode;
import com.example.concurrencycontrolproject.domain.schedule.exception.ScheduleException;
import com.example.concurrencycontrolproject.domain.schedule.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	private final ConcertRepository concertRepository;
	private final ScheduleRepository scheduleRepository;

	// 공연 스케줄 생성
	@Transactional
	public AdminScheduleResponse saveSchedule(AuthUser authUser, CreateScheduleRequest request) {

		Concert concert = findConcertById(request.getConcertId());
		Schedule schedule = Schedule.of(concert, request.getDateTime(), ScheduleStatus.ACTIVE);

		Schedule savedSchedule = scheduleRepository.save(schedule);
		return AdminScheduleResponse.of(savedSchedule);
	}

	// 공연 스케줄 다건 조회 (관리자)
	@Transactional(readOnly = true)
	public Page<AdminScheduleResponse> getAdminSchedules(AuthUser authUser, Long concertId, LocalDate date, int page, int size) {

		findConcertById(concertId);

		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.plusDays(1).atStartOfDay();

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("datetime").ascending());
		Page<Schedule> schedules = scheduleRepository.findByConcertIdAndDatetime(concertId, start, end, pageable);

		return schedules.map(AdminScheduleResponse::of);
	}

	// 공연 스케줄 다건 조회 (사용자)
	@Transactional(readOnly = true)
	public Page<UserScheduleResponse> getUserSchedules(AuthUser authUser, Long concertId, LocalDate date, int page, int size) {

		findConcertById(concertId);

		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.plusDays(1).atStartOfDay();

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("datetime").ascending());
		Page<Schedule> schedules = scheduleRepository.findActiveByConcertIdAndDatetime(concertId, start, end, pageable);

		return schedules.map(UserScheduleResponse::of);
	}

	// 공연 스케줄 단건 조회
	@Transactional(readOnly = true)
	public AdminScheduleResponse getSchedule(AuthUser authUser, Long concertId, Long scheduleId) {

		Schedule schedule = findScheduleByConcertIdAndId(concertId, scheduleId);

		return AdminScheduleResponse.of(schedule);
	}

	// 공연 스케줄 정보 수정
	@Transactional
	public AdminScheduleResponse updateSchedule(AuthUser authUser, Long concertId, Long scheduleId,
		UpdateScheduleRequest request) {

		Schedule schedule = findScheduleByConcertIdAndId(concertId, scheduleId);

		schedule.updateDateTime(request.getDateTime());
		return AdminScheduleResponse.of(schedule);
	}

	// 공연 스케줄 상태 수정
	@Transactional
	public AdminScheduleResponse updateScheduleStatus(AuthUser authUser, Long concertId, Long scheduleId,
		UpdateScheduleStatusRequest request) {

		Schedule schedule = findScheduleByConcertIdAndId(concertId, scheduleId);

		// 관리자는 ACTIVE나 CANCELED 상태로는 변경할 수 있지만,
		// DELETED 상태는 deleteSchedule() 메서드를 통해서만 변경할 수 있습니다.
		// 이 로직에서는 DELETED 상태로의 변경을 막고 있습니다.
		if (request.getStatus() == ScheduleStatus.DELETED) {
			throw new ScheduleException(ScheduleErrorCode.CANNOT_CHANGE_DELETED_STATUS);
		}

		schedule.updateStatus(request.getStatus());
		return AdminScheduleResponse.of(schedule);
	}

	// 공연 스케줄 삭제
	@Transactional
	public void deleteSchedule(AuthUser authUser, Long concertId, Long scheduleId) {

		Schedule schedule = findScheduleByConcertIdAndId(concertId, scheduleId);
		schedule.deletedAt();
	}

	// 검증 로직
	private Concert findConcertById(Long concertId) {
		return concertRepository.findById(concertId)
			.orElseThrow(() -> new ScheduleException(ScheduleErrorCode.CONCERT_NOT_FOUND));
	}

	private Schedule findScheduleByConcertIdAndId(Long concertId, Long scheduleId) {
		return scheduleRepository.findByIdAndConcertId(concertId, scheduleId)
			.orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
	}
}