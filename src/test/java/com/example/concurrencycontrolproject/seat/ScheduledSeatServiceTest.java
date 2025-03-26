package com.example.concurrencycontrolproject.seat;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import com.example.concurrencycontrolproject.domain.seat.entity.ScheduledSeat;
import com.example.concurrencycontrolproject.domain.seat.repository.ScheduledSeatRepository;
import com.example.concurrencycontrolproject.domain.seat.service.ScheduledSeatService;

@ExtendWith(MockitoExtension.class) // Mockito 확장 사용
public class ScheduledSeatServiceTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private DefaultRedisScript<Long> redisScript;

	@Mock
	private ScheduledSeatRepository scheduledSeatRepository;

	@InjectMocks
	private ScheduledSeatService scheduledSeatService;

	private final Long scheduleId = 1L;
	private final Long seatId = 100L;
	private final Long userId = 999L;
	private final String redisKey = "scheduled_seat:" + scheduleId + ":" + seatId;

	@Test
	void 예약_성공_하면_Redis에_저장() {
		// given

		when(redisTemplate.execute(eq(redisScript), anyList(), any()))
			.thenReturn(1L); //
		ScheduledSeat mockSeat = new ScheduledSeat(redisKey, scheduleId, seatId, true, userId);

		//when
		when(scheduledSeatRepository.save(any(ScheduledSeat.class)))
			.thenReturn(mockSeat);

		boolean result = scheduledSeatService.reserveSeat(scheduleId, seatId, userId);

		// then
		assertThat(result).isTrue();
		verify(redisTemplate, times(1)).execute(eq(redisScript), anyList(), any());
		verify(scheduledSeatRepository, times(1)).save(any(ScheduledSeat.class));
	}

	@Test
	void testReserveSeat_Fail_WhenAlreadyReserved() {
		// Redis Lua 스크립트가 실행되었을 때 0(실패) 반환
		when(redisTemplate.execute(eq(redisScript), anyList(), any()))
			.thenReturn(0L);

		boolean result = scheduledSeatService.reserveSeat(scheduleId, seatId, userId);

		//then
		assertThat(result).isFalse();
		verify(scheduledSeatRepository, never()).save(any(ScheduledSeat.class));
	}

	@Test
	void testCancelReservation() {
		// 예약 취소
		scheduledSeatService.cancelReservation(scheduleId, seatId);

		verify(scheduledSeatRepository, times(1)).deleteById(redisKey);
		verify(redisTemplate, times(1)).delete(redisKey);
	}

	@Test
	void testGetReservation_Found() {
		ScheduledSeat mockSeat = new ScheduledSeat(redisKey, scheduleId, seatId, true, userId);
		when(scheduledSeatRepository.findById(redisKey))
			.thenReturn(Optional.of(mockSeat));

		ScheduledSeat result = scheduledSeatService.getReservation(scheduleId, seatId);

		assertThat(result).isNotNull();
		assertThat(result.getScheduleId()).isEqualTo(scheduleId);
		assertThat(result.getSeatId()).isEqualTo(seatId);
		assertThat(result.getReservedBy()).isEqualTo(userId);
	}

	@Test
	void testGetReservation_NotFound() {
		when(scheduledSeatRepository.findById(redisKey))
			.thenReturn(Optional.empty());

		ScheduledSeat result = scheduledSeatService.getReservation(scheduleId, seatId);

		assertThat(result).isNull();
	}
}

