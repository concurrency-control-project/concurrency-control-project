package com.example.concurrencycontrolproject.seat;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import com.example.concurrencycontrolproject.domain.common.response.Response;
import com.example.concurrencycontrolproject.domain.seat.dto.scheduledSeat.ScheduledSeatResponseDTO;
import com.example.concurrencycontrolproject.domain.seat.entity.scheduledSeat.ScheduledSeat;
import com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat.ScheduledSeatErrorCode;
import com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat.ScheduledSeatException;
import com.example.concurrencycontrolproject.domain.seat.repository.scheduledSeat.ScheduledSeatRepository;
import com.example.concurrencycontrolproject.domain.seat.service.scheduledSeat.ScheduledSeatService;

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

	// @BeforeEach
	// void setUp() {
	// 	// 기본적으로 Redis Lua 스크립트 실행 시 1L(성공) 반환하도록 설정
	// 	when(redisTemplate.execute(eq(redisScript), anyList(), any())).thenReturn(1L);
	// }

	@Test
	void testReserveSeat_Success() {
		// 예약 성공 시 Redis에 저장된다고 가정
		ScheduledSeat scheduledSeat = new ScheduledSeat(redisKey, scheduleId, seatId, true, userId);

		when(redisTemplate.execute(eq(redisScript), anyList(), any())).thenReturn(1L);
		when(scheduledSeatRepository.save(any(ScheduledSeat.class)))
			.thenReturn(scheduledSeat);

		Response<String> response = scheduledSeatService.reserveSeat(scheduleId, seatId, userId);

		assertThat(response.getData()).isEqualTo("좌석 예약 성공!");
		verify(redisTemplate, times(1)).execute(eq(redisScript), anyList(), any());
		verify(scheduledSeatRepository, times(1)).save(any(ScheduledSeat.class));
	}

	@Test
	void testReserveSeat_Fail_WhenAlreadyReserved() {
		// Redis Lua 스크립트가 0을 반환하면 좌석이 이미 예약된 상태
		when(redisTemplate.execute(eq(redisScript), anyList(), any())).thenReturn(0L);
		
		ScheduledSeatException exception = assertThrows(
			ScheduledSeatException.class,
			() -> scheduledSeatService.reserveSeat(scheduleId, seatId, userId)
		);

		assertThat(exception.getMessage()).isEqualTo(ScheduledSeatErrorCode.SEAT_ALREADY_RESERVED.getDefaultMessage());
		verify(scheduledSeatRepository, never()).save(any(ScheduledSeat.class));
	}

	@Test
	void testCancelReservation_Success() {
		// 예약된 좌석이 존재하는 경우
		when(scheduledSeatRepository.existsById(redisKey)).thenReturn(true);

		Response<String> response = scheduledSeatService.cancelReservation(scheduleId, seatId);

		assertThat(response.getData()).isEqualTo("좌석 예약 취소 완료!");
		verify(scheduledSeatRepository, times(1)).deleteById(redisKey);
		verify(redisTemplate, times(1)).delete(redisKey);
	}

	@Test
	void testCancelReservation_Fail_WhenSeatNotFound() {
		// 예약된 좌석이 존재하지 않는 경우
		when(scheduledSeatRepository.existsById(redisKey)).thenReturn(false);

		ScheduledSeatException exception = assertThrows(
			ScheduledSeatException.class,
			() -> scheduledSeatService.cancelReservation(scheduleId, seatId)
		);

		assertThat(exception.getMessage()).isEqualTo(ScheduledSeatErrorCode.SEAT_NOT_FOUND.getDefaultMessage());
		verify(scheduledSeatRepository, never()).deleteById(redisKey);
		verify(redisTemplate, never()).delete(redisKey);
	}

	@Test
	void testGetReservation_Success() {
		ScheduledSeat scheduledSeat = new ScheduledSeat(redisKey, scheduleId, seatId, true, userId);
		when(scheduledSeatRepository.findById(redisKey)).thenReturn(Optional.of(scheduledSeat));

		Response<ScheduledSeatResponseDTO> response = scheduledSeatService.getReservation(scheduleId, seatId);

		assertThat(response.getData()).isNotNull();
		assertThat(response.getData().getScheduleId()).isEqualTo(scheduleId);
		assertThat(response.getData().getSeatId()).isEqualTo(seatId);
		assertThat(response.getData().getReservedBy()).isEqualTo(userId);
	}

	@Test
	void testGetReservation_Fail_WhenSeatNotFound() {
		when(scheduledSeatRepository.findById(redisKey)).thenReturn(Optional.empty());

		ScheduledSeatException exception = assertThrows(
			ScheduledSeatException.class,
			() -> scheduledSeatService.getReservation(scheduleId, seatId)
		);

		assertThat(exception.getMessage()).isEqualTo(ScheduledSeatErrorCode.SEAT_NOT_FOUND.getDefaultMessage());
	}
}

