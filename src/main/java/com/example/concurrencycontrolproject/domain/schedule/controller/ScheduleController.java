package com.example.concurrencycontrolproject.domain.schedule.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.schedule.dto.request.CreateScheduleRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.request.UpdateScheduleRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.request.UpdateScheduleStatusRequest;
import com.example.concurrencycontrolproject.domain.schedule.dto.response.AdminScheduleResponse;
import com.example.concurrencycontrolproject.domain.schedule.dto.response.UserScheduleResponse;
import com.example.concurrencycontrolproject.domain.schedule.service.ScheduleService;

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
	public ResponseEntity<AdminScheduleResponse> saveSchedule(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody CreateScheduleRequest request
	) {
		return ResponseEntity.ok(scheduleService.saveSchedule(authUser, request));
	}

	// 공연 스케줄 다건 조회 (관리자)
	@Secured(UserRole.Authority.ADMIN)
	@GetMapping("/v1/admin/concerts/{concertId}/schedules") // 사용자 다건 조회 엔드포인트와 경로 충돌을 방지하기 위해 /admin 경로 추가
	public ResponseEntity<Page<AdminScheduleResponse>> getAdminSchedules(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@RequestParam LocalDate date,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.ok(scheduleService.getAdminSchedules(authUser, concertId, date, page, size));
	}

	// 공연 스케줄 다건 조회 (사용자)
	@GetMapping("/v1/concerts/{concertId}/schedules")
	public ResponseEntity<Page<UserScheduleResponse>> getUserSchedules(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@RequestParam LocalDate date
	) {
		return ResponseEntity.ok(scheduleService.getUserSchedules(authUser, concertId, date));
	}

	// 공연 스케줄 단건 조회
	@Secured(UserRole.Authority.ADMIN)
	@GetMapping("/v1/concerts/{concertId}/schedules/{scheduleId}")
	public ResponseEntity<AdminScheduleResponse> getSchedule(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@PathVariable Long scheduleId
	) {
		return ResponseEntity.ok(scheduleService.getSchedule(authUser, concertId, scheduleId));
	}

	// 공연 스케줄 정보 수정
	@Secured(UserRole.Authority.ADMIN)
	@PutMapping("/v1/concerts/{concertId}/schedules/{scheduleId}")
	public ResponseEntity<AdminScheduleResponse> updateSchedule(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@PathVariable Long scheduleId,
		@Valid @RequestBody UpdateScheduleRequest request
	) {
		return ResponseEntity.ok(
			scheduleService.updateSchedule(authUser, concertId, scheduleId, request));
	}

	// 공연 스케줄 상태 수정
	@Secured(UserRole.Authority.ADMIN)
	@PutMapping("/v1/concerts/{concertId}/schedules/{scheduleId}/status")
	public ResponseEntity<AdminScheduleResponse> updateScheduleStatus(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@PathVariable Long scheduleId,
		@Valid @RequestBody UpdateScheduleStatusRequest request
	) {
		return ResponseEntity.ok(
			scheduleService.updateScheduleStatus(authUser, concertId, scheduleId, request));
	}

	// 공연 스케줄 삭제 (soft delete)
	@Secured(UserRole.Authority.ADMIN)
	@DeleteMapping("/v1/concerts/{concertId}/schedules/{scheduleId}")
	public ResponseEntity<Void> deleteSchedule(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@PathVariable Long scheduleId
	) {
		scheduleService.deleteSchedule(authUser, concertId, scheduleId);
		return ResponseEntity.noContent().build();
	}
}
