package com.example.concurrencycontrolproject.domain.seat.service.scheduledSeat;

import java.util.Collections;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.common.response.Response;
import com.example.concurrencycontrolproject.domain.seat.dto.scheduledSeat.ScheduledSeatResponse;
import com.example.concurrencycontrolproject.domain.seat.entity.scheduledSeat.ScheduledSeat;
import com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat.ScheduledSeatErrorCode;
import com.example.concurrencycontrolproject.domain.seat.exception.scheduledSeat.ScheduledSeatException;
import com.example.concurrencycontrolproject.domain.seat.repository.scheduledSeat.ScheduledSeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduledSeatService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final DefaultRedisScript<Long> redisScript;
	private final ScheduledSeatRepository scheduledSeatRepository;

	// 좌석 예약
	public Response<ScheduledSeatResponse> reserveSeat(Long scheduleId, Long seatId, Long userId) {
		String redisKey = "scheduled_seat:" + scheduleId + ":" + seatId;
		List<String> keys = Collections.singletonList(redisKey);

		Long result = redisTemplate.execute(redisScript, keys, userId.toString());

		if (result == null || result == 0) {
			throw new ScheduledSeatException(ScheduledSeatErrorCode.SEAT_ALREADY_RESERVED);
		}

		ScheduledSeat scheduledSeat = new ScheduledSeat(redisKey, scheduleId, seatId, true, userId);
		scheduledSeatRepository.save(scheduledSeat);
		return Response.of(new ScheduledSeatResponse(scheduledSeat));
	}

	// 예약 취소
	public Response<ScheduledSeatResponse> cancelReservation(Long scheduleId, Long seatId) {
		String redisKey = "scheduled_seat:" + scheduleId + ":" + seatId;

		if (!scheduledSeatRepository.existsById(redisKey)) {
			throw new ScheduledSeatException(ScheduledSeatErrorCode.SEAT_NOT_FOUND);
		}

		ScheduledSeat scheduledSeat = scheduledSeatRepository.findById(redisKey)
			.orElseThrow(() -> new ScheduledSeatException(ScheduledSeatErrorCode.SEAT_NOT_FOUND));

		scheduledSeatRepository.deleteById(redisKey);
		redisTemplate.delete(redisKey);

		ScheduledSeatResponse responseDTO = new ScheduledSeatResponse(scheduledSeat);

		return Response.of(responseDTO);
	}

	// 예약 상태 조회
	public Response<ScheduledSeatResponse> getReservation(Long scheduleId, Long seatId) {
		String redisKey = "scheduled_seat:" + scheduleId + ":" + seatId;

		ScheduledSeat reservation = scheduledSeatRepository.findById(redisKey)
			.orElseThrow(() -> new ScheduledSeatException(ScheduledSeatErrorCode.SEAT_NOT_FOUND));

		ScheduledSeatResponse responseDTO = new ScheduledSeatResponse(reservation);
		return Response.of(responseDTO);
	}
}


