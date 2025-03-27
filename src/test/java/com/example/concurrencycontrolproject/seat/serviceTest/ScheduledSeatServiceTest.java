package com.example.concurrencycontrolproject.seat.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import com.example.concurrencycontrolproject.domain.common.response.Response;
import com.example.concurrencycontrolproject.domain.seat.dto.scheduledSeat.ScheduledSeatResponse;
import com.example.concurrencycontrolproject.domain.seat.entity.scheduledSeat.ScheduledSeat;
import com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat.ScheduledSeatErrorCode;
import com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat.ScheduledSeatException;
import com.example.concurrencycontrolproject.domain.seat.repository.scheduledSeat.ScheduledSeatRepository;
import com.example.concurrencycontrolproject.domain.seat.service.scheduledSeat.ScheduledSeatService;

@ExtendWith(MockitoExtension.class)
class ScheduledSeatServiceTest {

	@InjectMocks
	private ScheduledSeatService scheduledSeatService;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private DefaultRedisScript<Long> redisScript;

	@Mock
	private ScheduledSeatRepository scheduledSeatRepository;

	private final Long scheduleId = 1L;
	private final Long seatId = 1L;
	private final Long userId = 1L;
	private final String redisKey = "scheduled_seat:" + scheduleId + ":" + seatId;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void reserveSeat_Success() {
		// Given
		when(redisTemplate.execute(any(), anyList(), any())).thenReturn(1L);

		ScheduledSeat scheduledSeat = new ScheduledSeat(redisKey, scheduleId, seatId, true, userId);
		when(scheduledSeatRepository.save(any(ScheduledSeat.class))).thenReturn(scheduledSeat);

		// When
		Response<ScheduledSeatResponse> response = scheduledSeatService.reserveSeat(scheduleId, seatId, userId);

		// Then
		assertNotNull(response);
		assertEquals(scheduleId, response.getData().getScheduleId());
		assertEquals(seatId, response.getData().getSeatId());
		verify(scheduledSeatRepository, times(1)).save(any(ScheduledSeat.class));
	}

	@Test
	void reserveSeat_Fail_SeatAlreadyReserved() {
		// Given: Redis에서 이미 예약된 경우
		when(redisTemplate.execute(any(), anyList(), any())).thenReturn(0L);

		// When & Then: 예외 발생 검증
		ScheduledSeatException exception = assertThrows(ScheduledSeatException.class,
			() -> scheduledSeatService.reserveSeat(scheduleId, seatId, userId));

		assertEquals(ScheduledSeatErrorCode.SEAT_ALREADY_RESERVED, exception.getErrorCode());
	}

	@Test
	void cancelReservation_Success() {
		// Given
		ScheduledSeat scheduledSeat = new ScheduledSeat(redisKey, scheduleId, seatId, true, userId);
		when(scheduledSeatRepository.existsById(redisKey)).thenReturn(true);
		when(scheduledSeatRepository.findById(redisKey)).thenReturn(Optional.of(scheduledSeat));

		// When
		Response<ScheduledSeatResponse> response = scheduledSeatService.cancelReservation(scheduleId, seatId);

		// Then
		assertNotNull(response);
		assertEquals(scheduleId, response.getData().getScheduleId());
		assertEquals(seatId, response.getData().getSeatId());
		verify(scheduledSeatRepository, times(1)).deleteById(redisKey);
		verify(redisTemplate, times(1)).delete(redisKey);
	}

	@Test
	void cancelReservation_Fail_SeatNotFound() {
		// Given: 좌석이 존재하지 않음
		when(scheduledSeatRepository.existsById(redisKey)).thenReturn(false);

		// When & Then: 예외 발생 검증
		ScheduledSeatException exception = assertThrows(ScheduledSeatException.class,
			() -> scheduledSeatService.cancelReservation(scheduleId, seatId));

		assertEquals(ScheduledSeatErrorCode.SEAT_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void getReservation_Success() {
		// Given
		ScheduledSeat scheduledSeat = new ScheduledSeat(redisKey, scheduleId, seatId, true, userId);
		when(scheduledSeatRepository.findById(redisKey)).thenReturn(Optional.of(scheduledSeat));

		// When
		Response<ScheduledSeatResponse> response = scheduledSeatService.getReservation(scheduleId, seatId);

		// Then
		assertNotNull(response);
		assertEquals(scheduleId, response.getData().getScheduleId());
		assertEquals(seatId, response.getData().getSeatId());
	}

	@Test
	void getReservation_Fail_SeatNotFound() {
		// Given: 존재하지 않는 좌석
		when(scheduledSeatRepository.findById(redisKey)).thenReturn(Optional.empty());

		// When & Then: 예외 발생 검증
		ScheduledSeatException exception = assertThrows(ScheduledSeatException.class,
			() -> scheduledSeatService.getReservation(scheduleId, seatId));

		assertEquals(ScheduledSeatErrorCode.SEAT_NOT_FOUND, exception.getErrorCode());
	}
}
