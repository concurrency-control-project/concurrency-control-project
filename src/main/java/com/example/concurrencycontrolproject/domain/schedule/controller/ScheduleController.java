package com.example.concurrencycontrolproject.domain.schedule.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.schedule.dto.request.CreateScheduleRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.request.UpdateScheduleRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.request.UpdateScheduleStatusRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.response.AdminScheduleResponse;
import com.example.concurrencycontrolproject.domain.schedule.dto.response.UserScheduleResponse;
import com.example.concurrencycontrolproject.domain.schedule.service.ScheduleService;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;
import com.example.concurrencycontrolproject.domain.common.response.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {

	private final ScheduleService scheduleService;

	// 공연 스케줄 생성
	@Secured(UserRole.Authority.ADMIN)
	@PostMapping("/v1/concerts/{concertId}/schedules")
	public Response<AdminScheduleResponse> saveSchedule(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody CreateScheduleRequest request
	) {
		return Response.of(scheduleService.saveSchedule(authUser, request));
	}

	// 공연 스케줄 다건 조회 (관리자)
	@Secured(UserRole.Authority.ADMIN)
	@GetMapping("/v1/admin/concerts/{concertId}/schedules") // 사용자 다건 조회 엔드포인트와 경로 충돌을 방지하기 위해 /admin 경로 추가
	public Response<AdminScheduleResponse> getAdminSchedules(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@RequestParam LocalDate date,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<AdminScheduleResponse> schedules = scheduleService.getAdminSchedules(authUser, concertId, date, page,
			size);
		return Response.fromPage(schedules);
	}

	// 공연 스케줄 다건 조회 (사용자)
	@GetMapping("/v1/concerts/{concertId}/schedules")
	public Response<UserScheduleResponse> getUserSchedules(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@RequestParam LocalDate date,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<UserScheduleResponse> schedules = scheduleService.getUserSchedules(authUser, concertId, date, page, size);
		return Response.fromPage(schedules);
	}

	// 공연 스케줄 단건 조회
	@Secured(UserRole.Authority.ADMIN)
	@GetMapping("/v1/concerts/{concertId}/schedules/{scheduleId}")
	public Response<AdminScheduleResponse> getSchedule(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@PathVariable Long scheduleId
	) {
		return Response.of(scheduleService.getSchedule(authUser, concertId, scheduleId));
	}

	// 공연 스케줄 정보 수정
	@Secured(UserRole.Authority.ADMIN)
	@PutMapping("/v1/concerts/{concertId}/schedules/{scheduleId}")
	public Response<AdminScheduleResponse> updateSchedule(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@PathVariable Long scheduleId,
		@Valid @RequestBody UpdateScheduleRequest request
	) {
		return Response.of(scheduleService.updateSchedule(authUser, concertId, scheduleId, request));
	}

	// 공연 스케줄 상태 수정
	@Secured(UserRole.Authority.ADMIN)
	@PutMapping("/v1/concerts/{concertId}/schedules/{scheduleId}/status")
	public Response<AdminScheduleResponse> updateScheduleStatus(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@PathVariable Long scheduleId,
		@Valid @RequestBody UpdateScheduleStatusRequest request
	) {
		return Response.of(scheduleService.updateScheduleStatus(authUser, concertId, scheduleId, request));
	}

	// 공연 스케줄 삭제 (soft delete)
	@Secured(UserRole.Authority.ADMIN)
	@DeleteMapping("/v1/concerts/{concertId}/schedules/{scheduleId}")
	public Response<Void> deleteSchedule(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@PathVariable Long scheduleId
	) {
		scheduleService.deleteSchedule(authUser, concertId, scheduleId);
		return Response.empty();
	}
}
